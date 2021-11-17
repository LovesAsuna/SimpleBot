package com.hyosakura.bot.dao

import com.hyosakura.bot.entity.dynamic.Dynamics
import com.hyosakura.bot.entity.dynamic.dynamics
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.find

class DynamicDao(override val database: Database) : DefaultDao {

    fun updateDynamic(upID: Long, dynamicID: String): Int {
        return database.update(Dynamics) {
            set(it.dynamicId, dynamicID)
            where { it.upId eq upID }
        }
    }

    fun getDynamicID(upID: Long): String? {
        return database.dynamics.find {
            it.upId eq upID
        }?.dynamicId
    }
}