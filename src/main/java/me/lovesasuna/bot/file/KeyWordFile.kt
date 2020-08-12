package me.lovesasuna.bot.file

import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.function.Misc
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.FileOutputStream
import java.io.ObjectOutputStream

object KeyWordFile : FileManipulate {
    override val file = BasicUtil.getLocation("keyword.json")
    lateinit var data: Misc.KeyWordChain
    override fun writeDefault() {
        val data = Misc.KeyWordChain()
        data.list.apply {
            add(Misc.KeyWord("啊这", "这啊", 10))
        }
        if (!file.exists()) {
            BotData.objectMapper!!.writerWithDefaultPrettyPrinter().writeValue(file, data)
        }
        readValue()
    }

    override fun writeValue() {
        ObjectOutputStream(FileOutputStream(file)).writeObject(data)
    }

    override fun readValue() {
        data = BotData.objectMapper!!.readValue(file, Misc.KeyWordChain::class.java)
    }
}