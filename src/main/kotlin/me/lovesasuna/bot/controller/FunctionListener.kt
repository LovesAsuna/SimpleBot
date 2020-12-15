package me.lovesasuna.bot.controller

import me.lovesasuna.bot.data.MessageBox
import java.io.IOException

/**
 * @author LovesAsuna
 */
interface FunctionListener {
    /**
     * @param box 消息封装体
     * @return 是否调用成功
     */
    @Throws(IOException::class)
    suspend fun execute(box: MessageBox): Boolean
}