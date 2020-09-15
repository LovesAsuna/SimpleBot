package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.KeyWordDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.KeyWordEntity
import me.lovesasuna.bot.service.KeyWordService
import org.hibernate.Session
import org.hibernate.SessionFactory

object KeyWordServiceImpl : KeyWordService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun addKeyWord(groupID: Long, wordRegex: String, reply: String, chance: Int): Boolean {
        session.beginTransaction()
        val dao = KeyWordDao(session)
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
        session.beginTransaction()
        val dao = KeyWordDao(session)
        return if (!dao.checkKeyWordExist(id)) {
            session.transaction.commit()
            false
        } else {
            dao.removeKeyWord(id)
            session.transaction.commit()
            false
        }
    }

    override fun getKeyWordsByGroup(groupID: Long) = KeyWordDao(session).getKeyWordsByGroup(groupID)

}