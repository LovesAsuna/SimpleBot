package me.lovesasuna.bot.util.interfaces

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import java.io.IOException

/**
 * @author LovesAsuna
 */
interface Listener {
    /**
     * @param event 群消息事件
     * @param message 群消息
     * @param image 图片
     * @param face QQ表情
     * @return 是否调用成功
     */
    @Throws(IOException::class)
    suspend fun execute(event: MessageEvent,message : String, image : Image?, face : Face?): Boolean
}