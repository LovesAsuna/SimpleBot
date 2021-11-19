package com.hyosakura.bot.entity.dynamic

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author LovesAsuna
 **/
object Dynamics : IntIdTable("dynamic") {
    val upId = long("up_id").uniqueIndex()
    val dynamicId = varchar("dynamic_id", 50).uniqueIndex()
}

class Dynamic(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Dynamic>(Dynamics)

    var upId by Dynamics.upId
    var dynamicId by Dynamics.dynamicId
    override fun toString(): String {
        return "Dynamic(upId=$upId, dynamicId='$dynamicId')"
    }
}