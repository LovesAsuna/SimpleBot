package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import org.hibernate.Session

class DynamicDao(override val session: Session) : DefaultHibernateDao<DynamicEntity>(session) {

    fun updateDynamic(upID: Int, context: String) {
        println("dao层更新前")
        session.update(DynamicEntity(null, upID, context))
        println("dao层更新完毕")
    }

    fun getContext(upID: Int): String {
        return queryField("select distinct e.context from DynamicEntity as e where upID = $upID", String::class.java).let {
            if (it.isEmpty()) {
                ""
            } else {
                it[0]
            }
        }
    }

}