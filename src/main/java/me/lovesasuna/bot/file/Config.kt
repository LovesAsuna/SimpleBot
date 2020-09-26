package me.lovesasuna.bot.file

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlMap
import kotlinx.serialization.Serializable
import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.ConfigData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import net.mamoe.mirai.utils.BotConfiguration

object Config {
    val file = BasicUtil.getLocation("config.yaml")
    lateinit var data: ConfigData

    fun writeDefault() {
        val data = ConfigData()
        if (!file.exists()) {
            file.writeText(Yaml.default.encodeToString(ConfigData.serializer(), data))
        }
        readValue()
    }

    private fun readValue() {
        data = Yaml.default.decodeFromString(ConfigData.serializer(), file.readText())
        Main.botConfig.protocol = BotConfiguration.MiraiProtocol.valueOf(data.Protocol.toUpperCase())
        if (data.Account == 0L || data.Password.isEmpty())
            throw AccountNotFoundException("账号信息未填写")
    }

}


