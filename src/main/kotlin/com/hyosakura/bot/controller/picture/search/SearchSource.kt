package com.hyosakura.bot.controller.picture.search

import com.hyosakura.bot.Main
import com.hyosakura.bot.util.BasicUtil
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl

/**
 * @author LovesAsuna
 **/
interface SearchSource<T> {
    val name: String
    suspend fun search(url: String): List<T>
}

suspend fun <T> CommandSender.getResult(source: SearchSource<T>, image: Image): List<T>? {
    val imgUrl = image.queryUrl()
    Main.logger.debug("图片URL: $imgUrl")
    val results = BasicUtil.withTimeOut({
        source.search(imgUrl)
    }, 35 * 1000) {
        sendMessage("搜索超时或发生异常: ${BasicUtil.debug(it.message ?: "未知错误！")}")
    }
    if (results == null) {
        return null
    } else {
        if (results.isEmpty()) {
            sendMessage("未查找到结果!")
            return null
        }
    }
    sendMessage("搜索完成!")
    return results
}