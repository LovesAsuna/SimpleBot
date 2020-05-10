package me.lovesasuna.bot.util

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.IOException

/**
 * @author LovesAsuna
 */
interface Listener {
    /**
     * @param builder 群消息事件
     * @param fromGroup 来自群
     * @param fromQQ 来自QQ
     * @param msg 来自消息
     * @return 是否调用成功
     */
    @Throws(IOException::class)
    suspend fun execute(event: MessageEvent,message : String, image : Image?, face : Face?): Boolean
}