package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.LinkDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.dynamic.LinkEntity
import me.lovesasuna.bot.service.LinkService
import org.hibernate.Session

object LinkServiceImpl : LinkService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: LinkDao by lazy { LinkDao(session) }

    override fun addLink(upID: Long, groupID: Long) {
        session.transaction.begin()
        try {
            dao.addLink(LinkEntity(null, groupID, upID))
        } finally {
            session.transaction.commit()
        }
    }

    override fun getUPByGroup(groupID: Long) = dao.getUPByGroup(groupID)

    override fun getGroupByUp(upID: Long) = dao.getGroupByUp(upID)

    override fun deleteGroup(groupID: Long): Int {
        session.transaction.begin()
        try {
            require(getGroups().contains(groupID))
            return dao.deleteGroup(groupID)
        } finally {
            session.transaction.commit()
        }
    }

    override fun deleteUp(upID: Long, groupID: Long): Int {
        session.transaction.begin()
        try {
            require(getUps().contains(upID))
            return dao.deleteUp(upID, groupID)
        } finally {
            session.transaction.commit()
        }
    }

    override fun getGroups() = dao.getGroups()

    override fun getUps() = dao.getUps()
}