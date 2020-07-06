package me.lovesasuna.bot.file

import me.lovesasuna.bot.Main
import me.lovesasuna.bot.function.Dynamic
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.*

object DynamicData : FileManipulate {
    private val dynamicFile = File(Main.dataFolder.toString() + File.separator + "dynamic.dat")

    override fun writeDefault() {
        throw UnsupportedOperationException("Nothing can be wrote out!")
    }

    override fun writeValue() {
        ObjectOutputStream(FileOutputStream(File(Main.dataFolder.toString() + File.separator + "dynamic.dat"))).writeObject(Dynamic.data)
    }

    override fun readValue() {
        if (dynamicFile.exists()) {
            Dynamic.data = ObjectInputStream(FileInputStream(dynamicFile)).readObject() as Dynamic.Data
        }
    }
}