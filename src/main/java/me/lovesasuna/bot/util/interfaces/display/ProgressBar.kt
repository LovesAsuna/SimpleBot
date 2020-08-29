package me.lovesasuna.bot.util.interfaces

import me.lovesasuna.bot.util.interfaces.display.ConsoleUI

interface ProgressBar : ConsoleUI {
    fun setInterval(interval: Long)
    suspend fun printWithInterval(interval: Long)
}