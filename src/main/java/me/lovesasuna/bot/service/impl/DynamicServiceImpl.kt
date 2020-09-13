package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.DynamicDao
import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.service.DynamicService
import org.hibernate.SessionFactory

object DynamicServiceImpl : DynamicService {

    override val factory: SessionFactory = BotData.HibernateConfig.buildSessionFactory()

    override fun update(upID: Int, context: String) {
        val session = factory.openSession()
        session.beginTransaction()
        println("use前")
        session.use { s ->
            println("service update前")
            DynamicDao(s).updateDynamic(upID, context)
            println("service更新完毕")
            session.transaction.commit()
        }
    }

    override fun getContext(upID: Int): String {
        val session = factory.openSession()
        session.use { s ->
            return DynamicDao(s).getContext(upID)
        }
    }

}