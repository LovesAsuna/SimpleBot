package com.hyosakura.bot.service

interface LinkService : DBService {
    fun addLink(upID: Long, groupID: Long): Boolean

    fun getUpByGroup(groupID: Long): List<Long>

    fun getGroupByUp(upID: Long): List<Long>

    fun deleteUpByGroup(upID: Long, groupID: Long): Int

    fun deleteGroup(groupID: Long): Int

    fun getGroups(): List<Long>

    fun getUps(): List<Long>
}