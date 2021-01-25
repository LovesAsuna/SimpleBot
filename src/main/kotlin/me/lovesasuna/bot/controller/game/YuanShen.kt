package me.lovesasuna.bot.controller.game

import me.lovesasuna.bot.Main
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

/**
 * @author LovesAsuna
 **/
object YuanShen : SimpleCommand(
    owner = Main,
    primaryName = "yuanshen"
) {
    @Handler
    suspend fun CommandSender.handle() {}
}