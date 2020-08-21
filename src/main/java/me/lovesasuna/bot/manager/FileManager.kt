package me.lovesasuna.bot.manager

import me.lovesasuna.bot.file.*
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import me.lovesasuna.bot.util.interfaces.file.FileManipulate
import java.io.File
import kotlin.jvm.Throws

object FileManager : AbstractFile() {
    private val fileList = setOf(
            Config, NoticeFile, DynamicFile, FunctionFilterFile, KeyWordFile
    )

    @Throws(AccountNotFoundException::class)
    override fun readValue() {
        fileList.forEach(FileManipulate::writeDefault)
        if (Config.data.account == 0L || Config.data.password.isEmpty())
            throw AccountNotFoundException("账号信息未填写")
    }

    override fun writeDefault() {
        throw UnsupportedOperationException()
    }

    override fun writeValue() {
        fileList.forEach(FileManipulate::writeValue)
    }
}