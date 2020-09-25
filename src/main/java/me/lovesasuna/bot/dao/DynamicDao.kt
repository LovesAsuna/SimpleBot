package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import org.hibernate.Session

class DynamicDao(override val session: Session) : DefaultHibernateDao<DynamicEntity>(session) {

    fun updateDynamic(entity: DynamicEntity) {
        session.saveOrUpdate(entity)
    }

    fun getContext(upID: Long): String {
        return queryField("select distinct e.context from DynamicEntity as e where upID = ?1", String::class.java, upID).let {
            if (it.isEmpty()) {
                ""
            } else {
                it[0]
            }
        }
    }

    fun getID(upID: Long): Int? {
        return queryField("select distinct e.id from DynamicEntity as e where upID = ?1", Int::class.javaObjectType, upID).let {
            if (it.isEmpty()) {
                null
            } else {
                it[0]
            }
        }
    }

    fun getEntity(upID: Long): DynamicEntity? {
        return queryEntity("from DynamicEntity as e where e.upID = ?1", DynamicEntity::class.java, 1).let {
            if (it.isEmpty()) {
                null
            } else {
                it[0]
            }
        }
    }
}