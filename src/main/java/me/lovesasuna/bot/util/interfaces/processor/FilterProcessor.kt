package me.lovesasuna.bot.util.interfaces.processor

interface FilterProcessor {
    fun filter(groupID: Long): Boolean {
        return false
    }
}