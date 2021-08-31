package com.hyosakura.bot.service

interface LinkService : DBService {
    fun addLink(upID: Long, groupID: Long)

    fun getUPByGroup(groupID: Long): List<Long>

    fun getGroupByUp(upID: Long): List<Long>

    fun deleteUp(upID: Long, groupID: Long): Int

    fun deleteGroup(groupID: Long): Int

    fun getGroups(): List<Long>

    fun getUps(): List<Long>
}