package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.dynamic.LinkEntity
import me.lovesasuna.bot.util.JL
import org.hibernate.Session

/**
 * @author LovesAsuna
 * @date 2020/9/12 18:13
 **/
class LinkDao(override val session: Session) : DefaultHibernateDao<LinkEntity>(session) {
    fun getUPByGroup(groupID: Long): List<Long> {
        return queryField("select distinct e.upID from LinkEntity as e where groupID = $groupID", JL::class.java) as List<Long>
    }

    fun getGroupByUp(upID: Long): List<Long> {
        return queryField("select distinct e.groupID from LinkEntity as e where upID = $upID", JL::class.java) as List<Long>
    }

    fun deleteUp(upID: Long, groupID: Long): Int {
        return update("delete from LinkEntity where upID = $upID and groupID = $groupID")
    }

    fun addLink(entity: LinkEntity) {
        session.saveOrUpdate(entity)
    }

    fun getGroups(): List<Long> {
        return queryField("select distinct e.groupID from LinkEntity as e", JL::class.java) as List<Long>
    }

    fun getUps(): List<Long> {
        return queryField("select distinct e.upID from LinkEntity as e", JL::class.java) as List<Long>
    }
}
