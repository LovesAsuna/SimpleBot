package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.console.plugins.ToBeRemoved

class Config {
    companion object {
        private lateinit var instance : Main
        lateinit var config: Config

        fun init(instance : Main) {
            this.instance = instance
            config = instance.loadConfig("config.yml")
            writeDefault()
        }

        @OptIn(ToBeRemoved::class)
        private fun writeDefault() {
            config.setIfAbsent("PictureSearchAPI", "")
            config.setIfAbsent("BilibiliCookie", "")
            config.save()
        }

    }
}