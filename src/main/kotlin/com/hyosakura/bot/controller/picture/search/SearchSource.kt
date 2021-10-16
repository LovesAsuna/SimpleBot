package com.hyosakura.bot.controller.picture.search

import com.hyosakura.bot.Main
import net.mamoe.mirai.console.command.ConsoleCommandSender.sendMessage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl

/**
 * @author LovesAsuna
 **/
interface SearchSource<T> {
    fun search(url: String): List<T>
}

suspend fun <T> getResult(source: SearchSource<T>, image: Image): List<T>? {
    val imgUrl = image.queryUrl()
    Main.logger.debug("图片URL: $imgUrl")
    val results = source.search(imgUrl)
    if (results.isEmpty()) {
        sendMessage("未查找到结果!")
        return null
    }
    sendMessage("搜索完成!")
    return results
}