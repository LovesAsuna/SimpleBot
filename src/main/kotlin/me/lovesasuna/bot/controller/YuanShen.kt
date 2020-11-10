package me.lovesasuna.bot.controller

import me.lovesasuna.bot.util.interfaces.FunctionListener
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image

/**
 * @author LovesAsuna
 **/
class YuanShen : FunctionListener {
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        return true
    }
}