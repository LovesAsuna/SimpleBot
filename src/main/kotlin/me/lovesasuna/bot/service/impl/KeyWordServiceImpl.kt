package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.KeyWordDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.KeyWordEntity
import me.lovesasuna.bot.service.KeyWordService
import org.hibernate.Session

object KeyWordServiceImpl : KeyWordService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: KeyWordDao by lazy { KeyWordDao(session) }
    
    override fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean {
        session.transaction.begin()
        val dao = dao
        return if (dao.checkKeyWordExist(groupID, wordRegex)) {
            session.transaction.commit()
            false
        } else {
            dao.addKeyWord(KeyWordEntity(null, groupID, wordRegex, reply, chance))
            session.transaction.commit()
            true
        }
    }

    override fun removeKeyWord(id: Int): Boolean {
        session.transaction.begin()
        val dao = dao
        return if (!dao.checkKeyWordExist(id)) {
            session.transaction.commit()
            false
        } else {
            dao.removeKeyWord(KeyWordEntity(id))
            session.transaction.commit()
            true
        }
    }

    override fun getKeyWordsByGroup(groupID: Long) = dao.getKeyWordsByGroup(groupID)

}