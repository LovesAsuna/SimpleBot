package com.hyosakura.bot.controller.misc

import com.fasterxml.jackson.databind.node.ObjectNode
import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.entity.misc.JikiPediaEntity
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.network.Request.toJson
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.getGroupOrNull
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL
import java.net.URLEncoder

object AdultDetector : SimpleCommand(
    owner = Main,
    primaryName = "adult",
    description = "未成年查询",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle(member: Member) {
        val url = "https://www.wegame.com.cn/api/middle/lua/realname/check_user_real_name"
        val mapper = BotData.objectMapper
        val body = mapper.createObjectNode()
        body.set<ObjectNode>(
            "qq_login_key", mapper.createObjectNode().put("qq_key_type", 3)
                .put("uint64_uin", member.id)
        ).put("acc_type", 1)
        val response = Request.postJson(url, body).toJson()
        val result = response["result"]
        if (result.asInt() != 0) {
            sendMessage("查询失败！Q号不存在或Q号有误")
        } else {
            val builder = StringBuilder()
            val isRealName = if (response["is_realname"].asInt() == 1) "true" else "false"
            val isAdult = if (response["is_adult"].asInt() == 1) "true" else "false"
            builder.append("是否实名: $isRealName\n")
                .append("成年: $isAdult")
            sendMessage(builder.toString())
        }
    }
}

object Baike : SimpleCommand(
    owner = Main,
    primaryName = "baike",
    description = "百度百科",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle(context: String) {
        val url = "https://baike.baidu.com/item/${URLEncoder.encode(context, "UTF-8")}"
        val root = Jsoup.parse(URL(url), 5000)
        sendMessage(root.select("meta[name=description]").attr("content").run {
            this.ifEmpty {
                "百度百科未收录此词条!"
            }
        })
    }
}

object DogLicking : SimpleCommand(
    owner = Main,
    primaryName = "舔狗日记",
    description = "舔狗日记",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun CommandSender.handle() {
        sendMessage(Request.getStr("https://v2.alapi.cn/api/dog?format=text&token=dppfgmdxhKZlt6vB"))
    }
}

object FisherMan : SimpleCommand(
    owner = Main,
    primaryName = "摸鱼人日历",
    description = "摸鱼人日历",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val root = Request.getJson("https://api.kukuqaq.com/tool/fishermanCalendar?preview")
        sendMessage(
            Request.getIs(root["data"]["url"].asText())
                .uploadAsImage(this.subject!!)
        )
    }
}

object GithubInfo : SimpleCommand(
    owner = Main,
    primaryName = "github",
    description = "获取github仓库信息",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(url: String) {
        try {
            sendMessage(
                Request.getIs(
                    Jsoup.parse(URL("https://ghproxy.com/https://github.com/$url"), 10000)
                        .head()
                        .getElementsByAttributeValue("property", "og:image")
                        .attr("content")
                ).uploadAsImage(this.subject!!)
            )
        } catch (e: IOException) {
            sendMessage("仓库不存在或连接超时！")
        }
    }
}

object Hitokoto : RawCommand(
    owner = Main,
    primaryName = "一言",
    description = "一言",
    parentPermission = registerDefaultPermission()
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        when (args.size) {
            0 -> {
                // 如果不带参数,默认全部获取
                getResult("https://v1.hitokoto.cn/", this)
            }
            1 -> {
                when (args[0].content) {
                    "help" -> {
                        sendMessage(
                            """
     一言参数: 
     a	Anime - 动画
     b	Comic – 漫画
     c	Game – 游戏
     d	Novel – 小说
     e	Myself – 原创
     f	Internet – 来自网络
     g	Other – 其他
     不填 - 随机
     """.trimIndent()
                        )
                    }
                    else -> {
                        getResult("https://v1.hitokoto.cn/?c=" + args[0], this)
                    }
                }
            }
        }
    }

    private suspend fun getResult(url: String, sender: CommandSender) {
        val `object` = Request.getJson(url)
        val hitokoto = `object`["hitokoto"].asText()
        val from = `object`["from"].asText()
        sender.sendMessage("『 $hitokoto 』- 「$from」")
    }
}

object JikiPedia : SimpleCommand(
    owner = Main,
    primaryName = "查梗",
    description = "查网络流行语",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(str: String) {
        val text = BotData.objectMapper.createObjectNode().put("phrase", str)
        val root = Request.postJson(
            "https://api.jikipedia.com/go/auto_complete",
            text,
            10000,
            mapOf("Client" to "Web")
        ).toJson()
        for (data in root["data"]) {
            JikiPediaEntity.parse(data)?.let {
                sendMessage(it.toString())
            }
        }
    }
}

object Nbnhhsh : SimpleCommand(
    owner = Main,
    primaryName = "nbnhhsh",
    description = "能不能好好说话?",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle(abbreviation: String) {
        val text = BotData.objectMapper.createObjectNode().put("text", abbreviation)
        sendMessage(
            "可能的结果: ${
                Request.postJson(
                    "https://lab.magiconch.com/api/nbnhhsh/guess",
                    text
                ).toJson()[0]["trans"] ?: "[]"
            }"
        )
    }
}

object News : SimpleCommand(
    owner = Main,
    primaryName = "60s",
    description = "每日新闻",
    parentPermission = registerDefaultPermission()
) {
    @Handler
    suspend fun CommandSender.handle() {
        val url = "https://api.03c3.cn/zb/"
        sendMessage(Request.getIs(url).run {
            uploadAsImage(getGroupOrNull()!!)
        })

    }
}

class PoisonousChickenSoup : FunctionListener {
    override suspend fun execute(box: MessageBox): Boolean {
        if (box.text() != "/毒鸡汤") {
            return false
        }
        box.reply(
            Request.getJson("https://v2.alapi.cn/api/soul?token=dppfgmdxhKZlt6vB")["data"]["content"].asText()
        )
        return true
    }
}