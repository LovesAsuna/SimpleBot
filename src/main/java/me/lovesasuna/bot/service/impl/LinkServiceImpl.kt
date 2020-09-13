package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.LinkDao
import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.entity.dynamic.LinkEntity
import me.lovesasuna.bot.service.LinkService
import org.hibernate.SessionFactory

object LinkServiceImpl : LinkService {

    override val factory: SessionFactory = BotData.HibernateConfig.buildSessionFactory()

    override fun addLink(upID: Int, groupID: Int) {
        val session = factory.openSession()
        session.beginTransaction()
        LinkDao(session).addLink(LinkEntity(null, groupID, upID))
        session.transaction.commit()
        session.close()
    }

    override fun getUPByGroup(groupID: Int): List<Int> {
        val session = factory.openSession()
        session.use { s ->
            return LinkDao(s).getUPByGroup(groupID)
        }
    }

    override fun getGroupByUp(upID: Int): List<Int> {
        val session = factory.openSession()
        session.use { s ->
            return LinkDao(s).getGroupByUp(upID)
        }
    }

    override fun deleteUp(upID: Int, groupID: Int): Int {
        val session = factory.openSession()
        session.beginTransaction()
        require(getUps().contains(upID))
        session.use { s ->
            val i =  LinkDao(s).deleteUp(upID, groupID)
            session.transaction.commit()
            return i
        }
    }

    override fun getGroups(): List<Int> {
        val session = factory.openSession()
        session.use { s ->
            return LinkDao(s).getGroups()
        }
    }

    override fun getUps(): List<Int> {
        val session = factory.openSession()
        session.use { s ->
            return LinkDao(s).getUps()
        }
    }
}