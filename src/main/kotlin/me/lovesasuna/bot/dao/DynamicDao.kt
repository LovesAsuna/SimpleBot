package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import org.hibernate.Session
import java.util.*

class DynamicDao(override val session: Session) : DefaultHibernateDao<DynamicEntity>(session) {

    fun updateDynamic(entity: DynamicEntity) {
        session.saveOrUpdate(entity)
    }

    fun getDynamicID(upID: Long): Optional<String> {
        return getField(upID, "dynamicID", String::class.java)
    }

    private fun <T> getField(upID: Long, fieldName: String, clazz: Class<T>): Optional<T> {
        return queryField(
            "select distinct e.${fieldName} from DynamicEntity as e where upID = ?1",
            clazz,
            upID
        ).let {
            Optional.ofNullable(it.getOrNull(0))
        }
    }


    fun getEntity(upID: Long): DynamicEntity? {
        return queryEntity("from DynamicEntity as e where e.upID = ?1", DynamicEntity::class.java, upID).let {
            if (it.isEmpty()) {
                null
            } else {
                it[0]
            }
        }
    }
}