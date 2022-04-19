package com.hyosakura.bot.controller.misc.bangumi

import com.hyosakura.bot.Config
import com.hyosakura.bot.util.network.Request
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.Jsoup

@Suppress("BlockingMethodInNonBlockingContext")
object GalGame : BangumiFunction {
    private val headers = mapOf(
        "Cookie" to Config.BangumiCookie,
        "Host" to "bangumi.tv",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36"
    )
    override val type: BangumiFunction.BangumiType = BangumiFunction.BangumiType.GAME

    override suspend fun getBangumi(contact: Contact, keyword: String): Flow<MessageChain> =
        withContext(Dispatchers.IO) {
            flow {
                val response =
                    Request.get("https://bangumi.tv/subject_search/${keyword}?cat=${type.id}", 10000, headers)
                val root = Jsoup.parse(response.bodyAsText())
                root.select("#browserItemList > li").run { subList(0, if (size > 3) 3 else size) }.forEach { outer ->
                    kotlin.runCatching {
                        withTimeout(30000) {
                            val link = "https://bangumi.tv/" + outer.select("a:first-child").attr("href")
                            val innerResponse = Request.get(link, 10000, headers)
                            val innerRoot = Jsoup.parse(innerResponse.bodyAsText())
                            val need = listOf("中文名", "开发", "剧本", "原画", "平台", "发行日期")
                            val img = innerRoot.select("#bangumiInfo a").attr("href")
                            val message = buildMessageChain {
                                +Request.getIs("https:$img").uploadAsImage(contact)
                                innerRoot.select("#infobox li").forEach { inner ->
                                    val text = inner.text()
                                    if (need.stream().anyMatch {
                                            text.contains(it)
                                        }) {
                                        +"${text}\n"
                                    }
                                }
                                innerRoot.select("#browserItemList > li").forEach {
                                    it.select(".avatarNeue").attr("style").let { s ->
                                        +Request.getIs("https:${s.substring(22, s.length - 2)}")
                                            .uploadAsImage(contact)
                                    }
                                    +"\n${it.select(".tip_j").text()}"
                                }
                            }
                            emit(message)
                        }
                    }.onFailure {
                        contact.sendMessage("获取超时!")
                    }
                }
            }
        }
}