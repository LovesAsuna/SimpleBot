package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.BasicUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Config {
    companion object {
        private lateinit var instance: Main
        lateinit var data: Data

        fun init() {
            writeDefault()
        }

        private fun writeDefault() {
            val file = File("config.json")
            data = if (!file.exists()) {
                Files.createFile(Paths.get(file.path))
                val data = Data()
                BotData.objectMapper!!.writerWithDefaultPrettyPrinter().writeValue(BasicUtil.getLocation("config.json"), data)
                data
            } else {
                BotData.objectMapper!!.readValue(BasicUtil.getLocation("config.json"), Data::class.java)
            }

        }

    }
}

data class Data(var account: Long = 0,
                var admin: Long = 0,
                var password: String = "",
                var pictureSearchAPI: String = "",
                var bilibiliCookie: String = "")
