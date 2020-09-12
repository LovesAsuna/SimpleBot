package me.lovesasuna.bot.dao

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.entity.DynamicEntity
import org.hibernate.Session

class DynamicDao(override val session: Session) : DefaultHibernateDao<DynamicEntity>(session) {
    fun removeUP(id: Long) {
        val listString  = queryField("select e.upSet from DynamicEntity as e", String::class.java)[0]
        @Suppress("unchecked")
        val list = jacksonObjectMapper().readValue(listString, List::class.java).toMutableList()
        list.remove(1)
        println(list)
    }
}