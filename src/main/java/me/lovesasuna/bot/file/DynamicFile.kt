package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.Dynamic
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.*

object DynamicFile : AbstractFile() {
    override val file = File(Main.dataFolder.toString() + File.separator + "dynamic.dat")

    override fun writeDefault() {
        readValue()
    }

    override fun writeValue() {
        ObjectOutputStream(FileOutputStream(File(Main.dataFolder.toString() + File.separator + "dynamic.dat"))).writeObject(Dynamic.data)
    }

    override fun readValue() {
        if (file.exists()) {
            Dynamic.data = ObjectInputStream(FileInputStream(file)).readObject() as Dynamic.Data
        }
    }
}