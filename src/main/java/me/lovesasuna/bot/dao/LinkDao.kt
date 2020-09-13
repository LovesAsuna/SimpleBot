package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.dynamic.LinkEntity
import org.hibernate.Session

/**
 * @author LovesAsuna
 * @date 2020/9/12 18:13
 **/
class LinkDao(override val session: Session) : DefaultHibernateDao<LinkEntity>(session) {
    fun getUPByGroup(groupID: Int): List<Int> {
        return queryField("select distinct e.upID from LinkEntity as e where groupID = $groupID", Integer::class.java) as List<Int>
    }

    fun getGroupByUp(upID: Int): List<Int> {
        return queryField("select distinct e.groupID from LinkEntity as e where upID = $upID", Integer::class.java) as List<Int>
    }

    fun deleteUp(upID: Int, groupID: Int): Int {
        return update("delete from LinkEntity where upID = $upID and groupID = $groupID")
    }

    fun addLink(entity: LinkEntity) {
        session.saveOrUpdate(entity)
    }

    fun getGroups(): List<Int> {
        return queryField("select distinct e.groupID from LinkEntity as e", Integer::class.java) as List<Int>
    }

    fun getUps(): List<Int> {
        return queryField("select distinct e.upID from LinkEntity as e", Integer::class.java) as List<Int>
    }
}