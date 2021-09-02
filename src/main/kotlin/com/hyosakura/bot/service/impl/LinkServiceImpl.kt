package com.hyosakura.bot.service.impl

import com.hyosakura.bot.dao.LinkDao
import com.hyosakura.bot.data.BotData
import com.hyosakura.bot.entity.dynamic.LinkEntity
import com.hyosakura.bot.service.LinkService
import org.hibernate.Session

object LinkServiceImpl : LinkService {

    override val session: Session = BotData.functionConfig.buildSessionFactory().openSession()

    private val dao: LinkDao by lazy { LinkDao(session) }

    override fun addLink(upID: Long, groupID: Long) {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            dao.addLink(LinkEntity(null, groupID, upID))
        } finally {
            session.transaction.commit()
        }
    }

    override fun getUPByGroup(groupID: Long) = dao.getUPByGroup(groupID)

    override fun getGroupByUp(upID: Long) = dao.getGroupByUp(upID)

    override fun deleteGroup(groupID: Long): Int {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
        try {
            require(getGroups().contains(groupID))
            return dao.deleteGroup(groupID)
        } finally {
            session.transaction.commit()
        }
    }

    override fun deleteUp(upID: Long, groupID: Long): Int {
        if (!session.transaction.isActive) {
            session.beginTransaction()
        }
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