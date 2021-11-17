package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.dynamic.Link
import com.hyosakura.bot.entity.dynamic.dynamics
import com.hyosakura.bot.entity.dynamic.links
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.*

/**
 * @author LovesAsuna
 **/
class LinkDao(override val database: Database) : DefaultDao {
    fun existLink(upId: Long, groupId: Long): Boolean {
        return database.links.find {
            (it.upId eq upId) and (it.groupId eq groupId)
        } != null
    }

    fun existGroup(groupId: Long): Boolean {
        return database.links.find {
            it.groupId eq groupId
        } != null
    }

    fun getUpByGroup(groupId: Long): List<Long> {
        return database.links.filter {
            it.groupId eq groupId
        }.map {
            it.dynamic.upId
        }
    }

    fun getGroupByUp(upId: Long): List<Long> {
        return database.links.filter {
            it.upId eq upId
        }.map {
            it.groupId
        }
    }

    fun deleteUp(upId: Long, groupId: Long): Int {
        return database.links.removeIf {
            (it.upId eq upId) and (it.groupId eq groupId)
        }
    }

    fun deleteGroup(groupId: Long): Int {
        return database.links.removeIf {
            it.groupId eq groupId
        }
    }

    fun addLink(upId: Long, groupId: Long): Int {
        val link = Link {
            this.groupId = groupId
            this.dynamic = database.dynamics.find { it.upId eq upId }!!
        }
        return database.links.add(link)
    }

    fun getGroups(): List<Long> {
        return database.links.map {
            it.groupId
        }
    }

    fun getUps(): List<Long> {
        return database.links.map {
            it.dynamic.upId
        }
    }
}
