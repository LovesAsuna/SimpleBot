package me.lovesasuna.bot.service

interface LinkService : Service {
    fun addLink(upID: Int, groupID: Int)

    fun getUPByGroup(groupID: Int): List<Int>

    fun getGroupByUp(upID: Int): List<Int>

    fun deleteUp(upID: Int, groupID: Int): Int

    fun getGroups(): List<Int>

    fun getUps(): List<Int>
}