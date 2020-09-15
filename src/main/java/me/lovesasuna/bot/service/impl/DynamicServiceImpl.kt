package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.DynamicDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import me.lovesasuna.bot.service.DynamicService
import org.hibernate.Session
import org.hibernate.SessionFactory

object DynamicServiceImpl : DynamicService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun update(upID: Long, context: String) {
        session.beginTransaction()
        val dao = DynamicDao(session)
        val id = dao.getID(upID)
        DynamicDao(session).updateDynamic(DynamicEntity(id, upID, context))
        session.transaction.commit()
    }

    override fun getContext(upID: Long) = DynamicDao(session).getContext(upID)

}