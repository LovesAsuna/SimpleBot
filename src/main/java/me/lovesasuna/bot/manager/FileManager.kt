package me.lovesasuna.bot.manager

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.function.Dynamic
import me.lovesasuna.bot.function.Notice
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import java.io.*


object FileManager {
    private val noticeFile = File(Main.dataFolder.toString() + File.separator + "notice.dat")
    private val dynamicFile = File(Main.dataFolder.toString() + File.separator + "dynamic.dat")

    @Throws(AccountNotFoundException::class)
    fun readValue() {
        Config.init()
        if (Config.data.account == 0L || Config.data.password.isEmpty()) throw AccountNotFoundException()

            if (noticeFile.exists()) {
                Notice.data = ObjectInputStream(FileInputStream(noticeFile)).readObject() as Notice.Data
            }

        if (dynamicFile.exists()) {
            Dynamic.data = ObjectInputStream(FileInputStream(dynamicFile)).readObject() as Dynamic.Data
        }
    }

    fun writeValue() {
        ObjectOutputStream(FileOutputStream(File(Main.dataFolder.toString() + File.separator + "notice.dat"))).writeObject(Notice.data)
        ObjectOutputStream(FileOutputStream(File(Main.dataFolder.toString() + File.separator + "dynamic.dat"))).writeObject(Dynamic.data)
    }
}