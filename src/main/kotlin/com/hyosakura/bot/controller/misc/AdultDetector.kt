package com.hyosakura.bot.controller.misc

import com.fasterxml.jackson.databind.node.ObjectNode
import com.hyosakura.bot.Main
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.network.Request.toJson
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Member

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