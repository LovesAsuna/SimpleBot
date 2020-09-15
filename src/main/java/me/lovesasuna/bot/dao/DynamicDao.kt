package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import me.lovesasuna.bot.util.JI
import org.hibernate.Session

class DynamicDao(override val session: Session) : DefaultHibernateDao<DynamicEntity>(session) {

    fun updateDynamic(entity: DynamicEntity) {
        session.saveOrUpdate(entity)
    }

    fun getContext(upID: Long): String {
        return queryField("select distinct e.context from DynamicEntity as e where upID = $upID", String::class.java).let {
            if (it.isEmpty()) {
                ""
            } else {
                it[0]
            }
        }
    }

    fun getID(upID: Long): Int? {
        return queryField("select distinct e.id from DynamicEntity as e where upID = $upID", JI::class.java).let {
            if (it.isEmpty()) {
                null
            } else {
                it[0] as Int
            }
        }
    }

}