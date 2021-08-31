package com.hyosakura.bot.service.impl

import me.lovesasuna.bot.dao.DynamicDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.dynamic.DynamicEntity
import me.lovesasuna.bot.service.DynamicService
import org.hibernate.Session

object DynamicServiceImpl : DynamicService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: DynamicDao by lazy { DynamicDao(session) }

    override fun update(upID: Long, dynamicID: String) {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            val dao = dao
            var entity = dao.getEntity(upID)
            if (entity == null) {
                entity = DynamicEntity(null, upID, dynamicID)
            }
            entity.dynamicID = dynamicID
            dao.updateDynamic(entity)
        } finally {
            session.transaction.commit()
        }
    }

    override fun getDynamicID(upID: Long): String = dao.getDynamicID(upID).orElse("")
}