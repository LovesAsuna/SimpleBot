package me.lovesasuna.bot.controller.game

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox

/**
 * @author LovesAsuna
 **/
class YuanShen : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        return true
    }
}