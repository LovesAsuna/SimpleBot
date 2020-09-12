package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.DynamicDao
import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.entity.DynamicEntity
import me.lovesasuna.bot.service.DynamicService
import org.hibernate.SessionFactory

object DynamicServiceImpl : DynamicService {

    override val factory: SessionFactory = BotData.HibernateConfig.buildSessionFactory()

    override fun save(entity: DynamicEntity) {

    }

    override fun update() {
        TODO("Not yet implemented")
    }
}