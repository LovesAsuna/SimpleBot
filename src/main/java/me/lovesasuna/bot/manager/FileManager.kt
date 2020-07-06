package me.lovesasuna.bot.manager

import me.lovesasuna.bot.file.Config
import me.lovesasuna.bot.file.DynamicData
import me.lovesasuna.bot.file.NoticeData
import me.lovesasuna.bot.util.exceptions.AccountNotFoundException
import me.lovesasuna.bot.util.interfaces.file.FileManipulate


object FileManager : FileManipulate {
    @Throws(AccountNotFoundException::class)
    override fun readValue() {
        Config.writeDefault()
        Config.readValue()
        if (Config.data.account == 0L || Config.data.password.isEmpty())
            throw AccountNotFoundException("账号信息未填写")

        DynamicData.readValue()
        NoticeData.readValue()
    }

    override fun writeDefault() {
        throw UnsupportedOperationException()
    }

    override fun writeValue() {
        DynamicData.writeValue()
        NoticeData.writeValue()
    }
}