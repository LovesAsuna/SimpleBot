package me.lovesasuna.bot.util.plugin

import me.lovesasuna.bot.Main

object Logger {
    fun log(message: String, level: LogLevel) {
        val logger = Main.logger
        when (level) {
            LogLevel.INFO -> logger?.info(message)
            LogLevel.WARNING -> logger?.warning(message)
            LogLevel.ERROR -> logger?.error(message)
            LogLevel.CONSOLE -> println("[Console] $message")
        }
    }
    fun log(message: Messages, level: LogLevel) {
        log(message.message, level)
    }

    enum class Messages(val message: String) {
        DOWNLOAD_DEPEN("正在获取依赖"),
        BOT_SHUTDOWN("正在关闭 机器人...");
    }

    enum class LogLevel {
        CONSOLE,
        INFO,
        WARNING,
        ERROR;
    }
}