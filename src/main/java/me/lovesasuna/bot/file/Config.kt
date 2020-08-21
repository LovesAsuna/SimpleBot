package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.ConfigData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import net.mamoe.mirai.utils.BotConfiguration

object Config : AbstractFile() {
    override val file = BasicUtil.getLocation("config.json")
    lateinit var data: ConfigData

    override fun writeDefault() {
        val data = ConfigData()
        if (!file.exists()) {
            BotData.objectMapper!!.writerWithDefaultPrettyPrinter().writeValue(file, data)
        }
        readValue()
    }

    override fun writeValue() {
        return
    }

    override fun readValue() {
        data = BotData.objectMapper!!.readValue(file, ConfigData::class.java)
        Main.botConfig.protocol = BotConfiguration.MiraiProtocol.valueOf(data.protocol.toUpperCase())
    }

}


