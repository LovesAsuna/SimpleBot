package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.LinkDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.LinkService
import org.ktorm.database.Database

object LinkServiceImpl : LinkService {
    override val database: Database = BotData.botDatabase
    private val dao: LinkDao by lazy { LinkDao(database) }

    override fun addLink(upID: Long, groupID: Long): Boolean {
        return database.useTransaction {
            if (!dao.existLink(upID, groupID)) {
                dao.addLink(upID, groupID)
            } else {
                -1
            }
        } > 0
    }

    override fun getUpByGroup(groupID: Long): List<Long> {
        return database.useTransaction {
            dao.getUpByGroup(groupID)
        }
    }

    override fun getGroupByUp(upID: Long) = dao.getGroupByUp(upID)

    override fun deleteGroup(groupID: Long): Int {
        return database.useTransaction {
            dao.deleteGroup(groupID)
        }
    }

    override fun deleteUpByGroup(upID: Long, groupID: Long): Int {
        return database.useTransaction {
            dao.deleteUp(upID, groupID)
        }
    }

    override fun getGroups() = dao.getGroups()

    override fun getUps() = dao.getUps()
}