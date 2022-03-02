package me.blq.standupapi.standups

import com.linecorp.kotlinjdsl.query.spec.predicate.PredicateSpec
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import me.blq.StandupBot.commons.*
import me.blq.standupapi.commons.Page
import me.blq.standupapi.commons.getEntityManager
import me.blq.standupapi.commons.getQueryFactory
import me.blq.standupapi.commons.pageQuery
import me.blq.standupapi.commons.singleOrNullQuery


class StandupRepositoryImpl : StandupRepository {
    override fun listStandups(filters: StandupFilters): Page<Standup> {
        val qf = getQueryFactory()
        return qf.pageQuery(filters.page) {
            select(entity(Standup::class))
            from(entity(Standup::class))
            orderBy(column(Standup::id).asc())

            var pred: PredicateSpec = column(Standup::id).equal(1)
            if (filters.name != null)
                pred = pred.and(column(Standup::name).equal(filters.name))

            where(pred)
        }
    }

    override fun getStandupByName(name: String): Standup? {
        val qf = getQueryFactory()
        return qf.singleOrNullQuery {
            select(entity(Standup::class))
            from(entity(Standup::class))
            where(column(Standup::name).equal(name))
        }
    }

    override fun getStandupById(id: Long): Standup? {
        val qf = getQueryFactory()
        return qf.singleOrNullQuery {
            select(entity(Standup::class))
            from(entity(Standup::class))
            fetch(Standup::standupUsers)
            fetch(StandupUser::user)
            where(column(Standup::id).equal(id))
        }
    }

    override fun save(s: Standup): Standup {
        return getEntityManager().merge(s)
    }
}
