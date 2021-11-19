package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.DynamicDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.DynamicService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DynamicServiceImpl : DynamicService {
    override val database: Database = BotData.botDatabase
    private val dao: DynamicDao by lazy { DynamicDao(database) }

    override fun insertOrUpdate(upID: Long, dynamicID: String): Int = transaction(database) {
        if (dao.getDynamicID(upID) == null) {
            dao.addDynamic(upID, dynamicID)
        } else {
            dao.updateDynamic(upID, dynamicID)
        }
    }

    override fun getDynamicID(upID: Long): String? = transaction(database) {
        dao.getDynamicID(upID)
    }
}