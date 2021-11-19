package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.dynamic.Dynamic
import com.hyosakura.bot.entity.dynamic.Dynamics
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update


class DynamicDao(override val database: Database) : DefaultDao {

    fun addDynamic(upID: Long, dynamicID: String): Int {
        val maxId = Dynamics.slice(Dynamics.id).selectAll().maxOf {
            it[Dynamics.id]
        }.value
        return Dynamic.new(maxId) {
            this.upId = upID
            this.dynamicId = dynamicID
        }.id.value
    }

    fun updateDynamic(upID: Long, dynamicID: String): Int {
        return Dynamics.update({ Dynamics.upId eq upID }) {
            it[dynamicId] = dynamicID
        }
    }

    fun getDynamicID(upID: Long): String? {
        return Dynamic.find {
            Dynamics.upId eq upID
        }.firstOrNull()?.dynamicId
    }
}