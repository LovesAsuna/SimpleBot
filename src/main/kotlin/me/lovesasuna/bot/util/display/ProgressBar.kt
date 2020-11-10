package me.lovesasuna.bot.util.interfaces

import me.lovesasuna.bot.util.display.ConsoleUI

interface ProgressBar : ConsoleUI {
    fun setInterval(interval: Long)
    suspend fun printWithInterval(interval: Long)
}