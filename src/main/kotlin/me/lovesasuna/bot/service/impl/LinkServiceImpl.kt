package me.lovesasuna.bot.service.impl

import me.lovesasuna.bot.dao.LinkDao
import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.entity.database.dynamic.LinkEntity
import me.lovesasuna.bot.service.LinkService
import org.hibernate.Session

object LinkServiceImpl : LinkService {

    override val session: Session = BotData.HibernateConfig.buildSessionFactory().openSession()

    override fun addLink(upID: Long, groupID: Long) {
        session.transaction.begin()
        LinkDao(session).addLink(LinkEntity(null, groupID, upID))
        session.transaction.commit()
    }

    override fun getUPByGroup(groupID: Long) = LinkDao(session).getUPByGroup(groupID)


    override fun getGroupByUp(upID: Long) = LinkDao(session).getGroupByUp(upID)

    override fun deleteUp(upID: Long, groupID: Long): Int {
        session.transaction.begin()
        require(getUps().contains(upID))
        val i = LinkDao(session).deleteUp(upID, groupID)
        session.transaction.commit()
        return i
    }

    override fun getGroups() = LinkDao(session).getGroups()

    override fun getUps() = LinkDao(session).getUps()
}