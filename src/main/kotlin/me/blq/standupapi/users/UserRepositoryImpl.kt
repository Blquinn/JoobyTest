package me.blq.standupapi.users

import com.linecorp.kotlinjdsl.querydsl.expression.column
import me.blq.standupapi.commons.getEntityManager
import me.blq.standupapi.commons.getQueryFactory
import me.blq.standupapi.commons.singleOrNullQuery

class UserRepositoryImpl : UserRepository {
    override fun create(user: User): User {
        return getEntityManager().merge(user)
    }

    override fun getById(id: Long): User? {
        return getQueryFactory().singleOrNullQuery {
            select(entity(User::class))
            from(entity(User::class))
            where(column(User::id).equal(id))
        }
    }

    override fun getByUsername(username: String): User? {
        return getQueryFactory().singleOrNullQuery {
            select(entity(User::class))
            from(entity(User::class))
            where(column(User::username).equal(username))
        }
    }
}