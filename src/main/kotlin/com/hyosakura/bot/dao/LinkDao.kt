package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.dynamic.Links
import org.jetbrains.exposed.sql.*


/**
 * @author LovesAsuna
 **/
class LinkDao(override val database: Database) : DefaultDao {
    fun existLink(upId: Long, groupId: Long): Boolean {
        return Links.select {
            (Links.dynamic eq upId) and (Links.groupId eq groupId)
        }.any()
    }

    fun existGroup(groupId: Long): Boolean {
        return Links.select {
            Links.groupId eq groupId
        }.any()
    }

    fun getUpByGroup(groupId: Long): List<Long> {
        return Links.slice(Links.dynamic).select {
            Links.groupId eq groupId
        }.map {
            it[Links.dynamic]
        }
    }

    fun getGroupByUp(upId: Long): List<Long> {
        return Links.slice(Links.groupId).select {
            Links.dynamic eq upId
        }.map {
            it[Links.groupId]
        }
    }

    fun deleteUp(upId: Long, groupId: Long): Int {
        return Links.deleteWhere {
            (Links.dynamic eq upId) and (Links.groupId eq groupId)
        }
    }

    fun deleteGroup(groupId: Long): Int {
        return Links.deleteWhere {
            Links.groupId eq groupId
        }
    }

    fun addLink(upId: Long, groupId: Long): Int {
        return Links.insertAndGetId {
            it[this.dynamic] = upId
            it[this.groupId] = groupId
        }.value
    }

    fun getGroups(): List<Long> {
        return Links.slice(Links.groupId).selectAll().map {
            it[Links.groupId]
        }
    }

    fun getUps(): List<Long> {
        return Links.slice(Links.dynamic).selectAll().map {
            it[Links.dynamic]
        }
    }
}
