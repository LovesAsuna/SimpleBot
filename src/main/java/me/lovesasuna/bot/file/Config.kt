package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import net.mamoe.mirai.utils.BotConfiguration

object Config : FileManipulate {
    private val file = BasicUtil.getLocation("config.json")
    lateinit var data: Data

    override fun writeDefault() {
        val data = Data()
        if (!file.exists()) {
            BotData.objectMapper!!.writerWithDefaultPrettyPrinter().writeValue(file, data)
        }
        this.data = data
    }

    override fun writeValue() {
        throw UnsupportedOperationException()
    }

    override fun readValue() {
        data = BotData.objectMapper!!.readValue(BasicUtil.getLocation("config.json"), Data::class.java)
        Main.botConfig.protocol = BotConfiguration.MiraiProtocol.valueOf(data.protocol.toUpperCase())
    }

}

data class Data(var protocol: String = "",
                var account: Long = 0,
                var admin: Long = 0,
                var password: String = "",
                var pictureSearchAPI: String = "",
                var bilibiliCookie: String = "")
