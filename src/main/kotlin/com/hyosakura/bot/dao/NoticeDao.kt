package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.`fun`.Notice
import com.hyosakura.bot.entity.`fun`.Notices
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select


/**
 * @author LovesAsuna
 **/
class NoticeDao(override val database: Database) : DefaultDao {
    fun getMatchMessage(groupId: Long, targetId: Long): String? {
        return Notices.select {
            (Notices.groupId eq groupId) and (Notices.targetId eq targetId)
        }.map {
            it[Notices.message]
        }.firstOrNull()
    }

    fun addNotice(groupID: Long, targetID: Long, message: String): Int {
        return Notice.new {
            this.groupId = groupID
            this.targetId = targetID
            this.message = message
        }.id.value
    }

    fun removeNotice(groupId: Long, targetId: Long): Int {
        return Notices.deleteWhere {
            (Notices.groupId eq groupId) and (Notices.targetId eq targetId)
        }
    }
}