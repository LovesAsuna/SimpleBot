package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.DynamicDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.service.DynamicService
import org.hibernate.Session

object DynamicServiceImpl : DynamicService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun update(upID: Long, context: String) {
        session.transaction.begin()
        val dao = DynamicDao(session)
        val entity = dao.getEntity(upID)
        entity?.let {
            it.context = context
            dao.updateDynamic(it)
        }
        session.transaction.commit()
    }

    override fun getContext(upID: Long) = DynamicDao(session).getContext(upID)

}