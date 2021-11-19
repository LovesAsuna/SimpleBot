package com.hyosakura.bot.entity.message

import org.jetbrains.exposed.sql.Table


/**
 * @author LovesAsuna
 */
object Relations : Table("relation") {
    val group = reference("group_id", Groups)
    val member = reference("member_id", Members)
    override val primaryKey: PrimaryKey = PrimaryKey(group, member)
}
