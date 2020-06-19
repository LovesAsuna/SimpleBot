package me.lovesasuna.bot.manager

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.Dynamic
import me.lovesasuna.bot.function.Notice
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

object FileManager {
    private val noticeFile = File(Main.instance.dataFolder.toString() + File.separator + "notice.dat")
    private val dynamicFile = File(Main.instance.dataFolder.toString() + File.separator + "dynamic.dat")

    fun readValue() {
        if (noticeFile.exists()) {
            val data = ObjectInputStream(FileInputStream(noticeFile)).readObject() as Notice.Data
            Notice.data = data
        }

        if (dynamicFile.exists()) {
            val data = ObjectInputStream(FileInputStream(dynamicFile)).readObject() as Dynamic.Data
            Dynamic.data = data
        }


    }
}