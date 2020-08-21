package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.data.NoticeData
import me.lovesasuna.bot.function.Notice
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.*

object NoticeFile : AbstractFile() {
    override val file = File(Main.dataFolder.toString() + File.separator + "notice.dat")

    override fun writeDefault() {
        readValue()
    }

    override fun writeValue() {
        ObjectOutputStream(FileOutputStream(File(Main.dataFolder.toString() + File.separator + "notice.dat"))).writeObject(Notice.data)
    }

    override fun readValue() {
        if (file.exists()) {
            Notice.data = ObjectInputStream(FileInputStream(file)).readObject() as NoticeData
        }
    }
}