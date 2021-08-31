package com.hyosakura.bot.service.impl

import me.lovesasuna.bot.dao.KeyWordDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.`fun`.KeyWordEntity
import me.lovesasuna.bot.service.KeyWordService
import org.hibernate.Session

object KeyWordServiceImpl : KeyWordService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: KeyWordDao by lazy { KeyWordDao(session) }

    override fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            return if (dao.checkKeyWordExist(groupID, wordRegex)) {
                false
            } else {
                dao.addKeyWord(KeyWordEntity(null, groupID, wordRegex, reply, chance))
                true
            }
        } finally {
            session.transaction.commit()
        }
    }

    override fun removeKeyWord(id: Int): Boolean {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            return if (!dao.checkKeyWordExist(id)) {
                false
            } else {
                dao.removeKeyWord(KeyWordEntity(id))
                true
            }
        } finally {
            session.transaction.commit()
        }
    }

    override fun getKeyWordsByGroup(groupID: Long) = dao.getKeyWordsByGroup(groupID)

}