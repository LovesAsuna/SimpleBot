package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.DynamicDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.database.dynamic.DynamicEntity
import me.lovesasuna.bot.service.DynamicService
import org.hibernate.Session

object DynamicServiceImpl : DynamicService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun update(upID: Long, dynamicID: String) {
        session.transaction.begin()
        val dao = DynamicDao(session)
        var entity = dao.getEntity(upID)
        if (entity == null) {
            entity = DynamicEntity(null, upID, dynamicID)
        }
        entity.dynamicID = dynamicID
        dao.updateDynamic(entity)
        session.transaction.commit()
    }

    override fun getDynamicID(upID: Long): String = DynamicDao(session).getDynamicID(upID).orElse("")

}