package me.lovesasuna.bot.file

import com.charleskorn.kaml.Yaml
import me.lovesasuna.bot.OriginMain
import me.lovesasuna.bot.data.ConfigData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import net.mamoe.mirai.utils.BotConfiguration.MiraiProtocol.*

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
        OriginMain.botConfig.protocol = when (data.Protocol.toUpperCase()) {
            ANDROID_PAD.name -> ANDROID_PAD
            ANDROID_PHONE.name -> ANDROID_PHONE
            ANDROID_WATCH.name -> ANDROID_WATCH
            else -> ANDROID_PAD
        }
        if (data.Account == 0L || data.Password.isEmpty())
            throw AccountNotFoundException("账号信息未填写")
    }

}


