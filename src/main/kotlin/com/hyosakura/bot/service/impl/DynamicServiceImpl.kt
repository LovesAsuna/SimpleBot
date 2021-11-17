package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.DynamicDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.DynamicService
import org.ktorm.database.Database

object DynamicServiceImpl : DynamicService {
    override val database: Database = BotData.botDatabase
    private val dao: DynamicDao by lazy { DynamicDao(database) }

    override fun update(upID: Long, dynamicID: String): Int {
        return database.useTransaction {
            dao.updateDynamic(upID, dynamicID)
        }
    }

    override fun getDynamicID(upID: Long): String? = dao.getDynamicID(upID)
}