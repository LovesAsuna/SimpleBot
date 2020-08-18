package me.lovesasuna.bot.manager

import me.lovesasuna.bot.file.*
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.File
import kotlin.jvm.Throws


object FileManager : FileManipulate {
    @Throws(AccountNotFoundException::class)
    override fun readValue() {
        Config.writeDefault()
        if (Config.data.account == 0L || Config.data.password.isEmpty())
            throw AccountNotFoundException("账号信息未填写")

        NoticeFile.readValue()
        DynamicFile.readValue()
        FunctionFilterFile.writeDefault()
        KeyWordFile.writeDefault()
    }

    override val file: File = File("")

    override fun writeDefault() {
        throw UnsupportedOperationException()
    }

    override fun writeValue() {
        DynamicFile.writeValue()
        NoticeFile.writeValue()
    }
}