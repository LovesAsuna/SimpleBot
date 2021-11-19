package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.LinkDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.LinkService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object LinkServiceImpl : LinkService {
    override val database: Database = BotData.botDatabase
    private val dao: LinkDao by lazy { LinkDao(database) }

    override fun addLink(upID: Long, groupID: Long): Boolean = transaction(database) {
        if (!dao.existLink(upID, groupID)) {
            dao.addLink(upID, groupID)
        } else {
            -1
        } > 0
    }

    override fun getUpByGroup(groupID: Long): List<Long> = transaction(database) {
        dao.getUpByGroup(groupID)
    }

    override fun getGroupByUp(upID: Long) = transaction(database) {
        dao.getGroupByUp(upID)
    }

    override fun deleteGroup(groupID: Long): Int = transaction(database) {
        dao.deleteGroup(groupID)
    }

    override fun deleteUpByGroup(upID: Long, groupID: Long): Int = transaction(database) {
        dao.deleteUp(upID, groupID)
    }

    override fun getGroups() = transaction(database) {
        dao.getGroups()
    }

    override fun getUps() = transaction(database) {
        dao.getUps()
    }
}