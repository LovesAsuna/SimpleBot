package com.hyosakura.bot.service

import com.hyosakura.bot.entity.message.Message
import java.time.LocalDateTime

/**
 * @author LovesAsuna
 **/
interface GroupRecordService : DBService {
    fun groupIsNull(groupID: Long): Boolean

    fun memberIsNull(memberID: Long): Boolean

    fun relationIsNull(memberID: Long, groupID: Long): Boolean

    fun addGroup(groupID: Long, name: String): Boolean

    fun addMember(memberID: Long, name: String): Boolean

    fun addRecord(message: String, time: LocalDateTime, memberID: Long, groupID: Long): Boolean

    fun addRelation(memberID: Long, groupID: Long): Boolean

    fun queryUserRecord(memberID: Long): List<Message>

    fun queryGroupRecord(groupID: Long): List<Message>

    fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<Message>
}