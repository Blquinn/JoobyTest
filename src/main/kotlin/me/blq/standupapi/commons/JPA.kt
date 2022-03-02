package me.blq.standupapi.commons

import com.linecorp.kotlinjdsl.QueryFactory
import com.linecorp.kotlinjdsl.QueryFactoryImpl
import com.linecorp.kotlinjdsl.query.creator.CriteriaQueryCreatorImpl
import com.linecorp.kotlinjdsl.query.creator.SubqueryCreatorImpl
import com.linecorp.kotlinjdsl.querydsl.CriteriaQueryDsl
import me.blq.standupapi.commons.Page
import me.blq.standupapi.commons.PageRequest
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import kotlin.math.min

/**
 * This file contains helpers for working with JPA.
 */

private val emThreadLocal = ThreadLocal<EntityManager>()

/**
 * Gets the contextual entity manager.
 */
fun getEntityManager(): EntityManager = emThreadLocal.get()

fun <R> EntityManagerFactory.withEntityManager(fn: (EntityManager) -> R): R {
    val em = this.createEntityManager()
    emThreadLocal.set(em)

    try {
        val ret = fn(em)
        if (em.isOpen)
            em.close()
        return ret
    } catch (ex: Throwable) {
        if (em.isOpen)
            em.close()
        throw ex
    } finally {
        emThreadLocal.remove()
    }
}

fun <R> EntityManagerFactory.withTransaction(fn: (EntityManager) -> R): R {
    return withHandle { db ->
        val tx = db.entityManager.transaction
        tx.begin()

        try {
            val ret = fn(db.entityManager)
            if (tx.isActive)
                tx.commit()
            ret
        } catch (ex: Throwable) {
            if (tx.isActive)
                tx.rollback()
            throw ex
        }
    }
}


private val qfThreadLocal = ThreadLocal<QueryFactory>()

// Note that this will only work inside of the scope of 'withQueryFactory'.
fun getQueryFactory(): QueryFactory = qfThreadLocal.get()

data class DbHandle(val entityManager: EntityManager, val queryFactory: QueryFactory)

fun <R> EntityManagerFactory.withHandle(fn: (DbHandle) -> R): R {
    return withEntityManager { em ->
        val qf = QueryFactoryImpl(
            criteriaQueryCreator = CriteriaQueryCreatorImpl(em),
            subqueryCreator = SubqueryCreatorImpl()
        )
        qfThreadLocal.set(qf)

        try {
            fn(DbHandle(em, qf))
        } finally {
            qfThreadLocal.remove()
        }
    }
}

// Determine if next page exists by always over fetching by one.
inline fun <reified T> QueryFactory.pageQuery(pr: PageRequest, noinline dsl: CriteriaQueryDsl<T>.() -> Unit): Page<T> {
    val rl = this.selectQuery(T::class.java, dsl)
        .setFirstResult((pr.page - 1) * pr.pageSize)
        .setMaxResults(pr.pageSize + 1).resultList

    val next = if (rl.size > pr.pageSize) pr.page + 1 else null
    return Page(next, rl.subList(0, min(pr.pageSize, rl.size)))
}

inline fun <reified T> QueryFactory.singleOrNullQuery(noinline dsl: CriteriaQueryDsl<T>.() -> Unit): T? =
    this.selectQuery(T::class.java, dsl)
        .setMaxResults(1)
        .resultList
        .firstOrNull()
