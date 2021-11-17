package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.KeyWordDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.service.KeyWordService
import org.ktorm.database.Database

object KeyWordServiceImpl : KeyWordService {
    override val database: Database = BotData.botDatabase
    private val dao: KeyWordDao by lazy { KeyWordDao(database) }

    override fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean {
        return database.useTransaction {
            if (!dao.checkKeyWordExist(groupID, wordRegex)) {
                dao.addKeyWord(groupID, wordRegex, reply, chance)
            } else {
                -1
            }
        } > 0
    }

    override fun removeKeyWord(id: Int): Boolean {
        return database.useTransaction {
            if (dao.checkKeyWordExist(id)) {
                dao.removeKeyWordById(id)
            } else {
                -1
            }
        } > 0
    }

    override fun getKeyWordsByGroup(groupID: Long) = dao.getKeyWordsByGroup(groupID)

}