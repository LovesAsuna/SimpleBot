package com.hyosakura.bot.controller.game

import com.fasterxml.jackson.databind.JsonNode
import com.hyosakura.bot.Main
import com.hyosakura.bot.util.network.Request
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import kotlin.system.measureTimeMillis

/**
 * @author LovesAsuna
 */
object RainbowSix : RawCommand(
    owner = Main,
    primaryName = "r6",
    description = "彩虹六号战绩查询",
    parentPermission = registerDefaultPermission()
) {

    override suspend fun CommandSender.onCommand(args: MessageChain) {
        when (args.size) {
            1 -> {
                val username = args[0].content
                normalCheck(this, username)
            }
            3 -> {
                if ("op" == args[1].content) {
                    val username = args[0].content
                    val operatorName = args[2].content
                    operatorCheck(this, username, operatorName)
                }
            }
        }
    }

    private suspend fun normalCheck(sender: CommandSender, username: String) {
        val builder = StringBuilder()
        val time = measureTimeMillis {
            val root = getRoot(username)
            val basicStat = root["Basicstat"][0]
            val level = basicStat["level"].asText()
            var historyMaxMMR = basicStat["max_mmr"].asText()
            val uuid = basicStat["user_id"].asText()
            val statCR = root["StatCR"]
            val model = statCR[0]["model"].asText()
            var rankWon = "0"
            var rankLost = "0"
            var rankKills = "0"
            var rankDeaths = "0"
            var rankMMR = "0"
            var casualWon = "0"
            var casualLost = "0"
            var casualKills = "0"
            var casualDeaths = "0"
            if (model == "casual") {
                if (statCR.size() > 1) {
                    rankWon = statCR[1]["won"].asText()
                    rankLost = statCR[1]["lost"].asText()
                    rankKills = statCR[1]["kills"].asText()
                    rankDeaths = statCR[1]["deaths"].asText()
                    rankMMR = statCR[1]["mmr"].asText()
                }
                casualWon = statCR[0]["won"].asText()
                casualLost = statCR[0]["lost"].asText()
                casualKills = statCR[0]["kills"].asText()
                casualDeaths = statCR[0]["deaths"].asText()
            } else {
                rankWon = statCR[0]["won"].asText()
                rankLost = statCR[0]["lost"].asText()
                rankKills = statCR[0]["kills"].asText()
                rankDeaths = statCR[0]["deaths"].asText()
                rankMMR = statCR[0]["mmr"].asText()
                if (statCR.size() > 1) {
                    casualWon = statCR[1]["won"].asText()
                    casualLost = statCR[1]["lost"].asText()
                    casualKills = statCR[1]["kills"].asText()
                    casualDeaths = statCR[1]["deaths"].asText()
                }
            }
            val statGeneral = root["StatGeneral"][0]
            val killAssists = statGeneral["killAssists"].asText()
            val kills = statGeneral["kills"].asText()
            val deaths = statGeneral["deaths"].asText()
            val meleeKills = statGeneral["meleeKills"].asText()
            val revives = statGeneral["revives"].asText()
            val headshot = statGeneral["headshot"].asText()
            val won = statGeneral["won"].asText()
            val lost = statGeneral["lost"].asText()
            historyMaxMMR = if (historyMaxMMR.toDouble() > rankMMR.toDouble()) historyMaxMMR else rankMMR
            builder.append("用户名: ").append(username).append(" ").append("等级: ").append(level).append("\n")
                .append("UUID: ").append(uuid).append("\n")
            builder.append("------------------排位数据------------------\n")
            builder.append("历史最高MMR: ").append(historyMaxMMR).append(" ").append("MMR: ").append(rankMMR).append("\n")
                .append("击杀: ").append(rankKills).append(" ").append("死亡: ").append(rankDeaths).append(" ")
                .append("K/D: ").append(String.format("%.4f", rankKills.toDouble() / rankDeaths.toDouble()))
                .append("\n")
                .append("胜利: ").append(rankWon).append(" ").append("失败: ").append(rankLost).append(" ").append("W/L: ")
                .append(String.format("%.4f", rankWon.toDouble() / rankLost.toDouble())).append("\n")
            builder.append("------------------休闲数据------------------\n")
            builder.append("击杀: ").append(casualKills).append(" ").append("死亡: ").append(casualDeaths).append("K/D: ")
                .append(String.format("%.4f", casualKills.toDouble() / casualDeaths.toDouble())).append("\n")
                .append("胜利: ").append(casualWon).append(" ").append("失败: ").append(casualLost).append(" ")
                .append("W/L: ").append(String.format("%.4f", casualWon.toDouble() / casualLost.toDouble()))
                .append("\n")
            builder.append("------------------其他数据------------------\n")
            builder.append("击杀: ").append(kills).append(" ").append("助攻: ").append(killAssists).append(" ")
                .append("刀杀: ").append(meleeKills).append(" ").append("爆头: ").append(headshot).append("\n")
                .append("死亡: ").append(deaths).append(" ").append("被救起: ").append(revives).append(" ").append("K/D: ")
                .append(String.format("%.4f", kills.toDouble() / deaths.toDouble())).append("\n")
                .append("胜利: ").append(won).append(" ").append("失败: ").append(lost).append(" ").append("W/L: ")
                .append(String.format("%.4f", won.toDouble() / lost.toDouble())).append("\n")

        }
        sender.sendMessage(builder.append(String.format("查询耗时%.2f秒", (time / 1000).toDouble())).toString())
    }

    private suspend fun operatorCheck(sender: CommandSender, username: String, operatorName: String) {
        val start = System.currentTimeMillis()
        val root = getRoot(username)
        val statOperator = root["StatOperator"]
        val size = statOperator.size()
        val operators: MutableList<String> = ArrayList()
        for (i in 0 until size) {
            var opName = statOperator[i]["name"].asText().lowercase()
            opName.apply {
                when {
                    contains("盲") -> opName = "jager"
                    contains("茫") -> opName = "capitao"
                    contains("脴") -> opName = "nokk"
                }
            }

            operators.add(opName)
        }
        val builder = StringBuilder()
        val strings = operators.parallelStream().filter { s: String -> s.lowercase().startsWith(operatorName) }
            .toArray { arrayOfNulls<String>(size) }

        val level = root["Basicstat"][0]["level"].asText()
        for (i in strings.indices) {
            builder.setLength(0)
            val operator = statOperator[operators.indexOf(strings[i])] ?: continue
            val operatorKills = operator["kills"].asText()
            val operatorDeaths = operator["deaths"].asText()
            val operatorWon = operator["won"].asText()
            val operatorLost = operator["lost"].asText()
            builder.append("用户名: ").append(username).append(" ").append("等级: ").append(level).append("\n")
            builder.append("-------------------------------------------\n")
            builder.append("干员: ").append(strings[i]).append(" ").append("击杀: ").append(operatorKills).append(" ")
                .append("死亡: ").append(operatorDeaths)
                .append(" ").append("K/D: ")
                .append(String.format("%.4f", operatorKills.toDouble() / operatorDeaths.toDouble())).append("\n")
                .append("胜利: ").append(operatorWon).append(" ").append("失败: ").append(operatorLost).append(" ")
                .append("W/L: ").append(String.format("%.4f", operatorWon.toDouble() / operatorLost.toDouble()))
            sender.sendMessage(builder.toString())
        }
        val end = System.currentTimeMillis()
        builder.setLength(0)
        if (strings.isEmpty()) {
            builder.append("数据不存在").append("\n")
        }
        sender.sendMessage(builder.append(String.format("查询耗时%.2f秒", (end - start).toDouble() / 1000)).toString())
    }

    private suspend fun getRoot(username: String): JsonNode {
        return Request.getJson(
            "https://www.r6s.cn/Stats?username=$username", 10000, mapOf(
                "referer" to "https://www.r6s.cn/stats.jsp?username=$username"
            )
        )
    }
}