package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.`fun`.Notice
import com.hyosakura.bot.entity.`fun`.notices
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find

/**
 * @author LovesAsuna
 **/
class NoticeDao(override val database: Database) : DefaultDao {
    fun getMatchMessage(groupId: Long, targetId: Long): String? {
        return database.notices.find {
            (it.groupId eq groupId) and (it.targetId eq targetId)
        }?.message
    }

    fun addNotice(groupID: Long, targetID: Long, message: String): Int {
        val notice = Notice {
            this.groupId = groupID
            this.targetId = targetID
            this.message = message
        }
        return database.notices.add(notice)
    }

    fun removeNotice(groupId: Long, targetId: Long): Int {
        return database.notices.find {
            (it.groupId eq groupId) and (it.targetId eq targetId)
        }?.delete() ?: 0
    }
}