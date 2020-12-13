package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.database.dynamic.LinkEntity
import org.hibernate.Session

/**
 * @author LovesAsuna
 **/
class LinkDao(override val session: Session) : DefaultHibernateDao<LinkEntity>(session) {
    fun getUPByGroup(groupID: Long): List<Long> {
        return queryField(
            "select distinct e.upID from LinkEntity as e where groupID = $groupID",
            Long::class.javaObjectType
        )
    }

    fun getGroupByUp(upID: Long): List<Long> {
        return queryField(
            "select distinct e.groupID from LinkEntity as e where upID = $upID",
            Long::class.javaObjectType
        )
    }

    fun deleteUp(upID: Long, groupID: Long): Int {
        return update("delete from LinkEntity where upID = $upID and groupID = $groupID")
    }

    fun addLink(entity: LinkEntity) {
        session.saveOrUpdate(entity)
    }

    fun getGroups(): List<Long> {
        return queryField("select distinct e.groupID from LinkEntity as e", Long::class.javaObjectType)
    }

    fun getUps(): List<Long> {
        return queryField("select distinct e.upID from LinkEntity as e", Long::class.javaObjectType)
    }
}
