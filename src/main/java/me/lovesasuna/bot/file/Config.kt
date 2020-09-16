package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.ConfigData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import net.mamoe.mirai.utils.BotConfiguration

object Config {
    val file = BasicUtil.getLocation("config.json")
    lateinit var data: ConfigData

    fun writeDefault() {
        val data = ConfigData()
        if (!file.exists()) {
            BotData.objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data)
        }
        readValue()
    }

    private fun readValue() {
        data = BotData.objectMapper.readValue(file, ConfigData::class.java)
        Main.botConfig.protocol = BotConfiguration.MiraiProtocol.valueOf(data.protocol.toUpperCase())
        if (data.account == 0L || data.password.isEmpty())
            throw AccountNotFoundException("账号信息未填写")
    }

}


