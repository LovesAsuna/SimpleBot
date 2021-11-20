package com.hyosakura.bot.entity.dynamic

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author LovesAsuna
 **/
object Links : IntIdTable("link") {
    val groupId = long("group_id")
    val dynamic = reference("up_id", Dynamics.upId)
}

class Link(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Link>(Links)

    var groupId by Links.groupId
    var dynamic by Dynamic referencedOn Links.dynamic
    override fun toString(): String {
        return "Link(groupId=$groupId, dybamic=$dynamic)"
    }
}