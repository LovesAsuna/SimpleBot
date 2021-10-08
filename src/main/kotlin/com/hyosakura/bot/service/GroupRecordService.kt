package com.hyosakura.bot.service

import com.hyosakura.bot.entity.message.MessageEntity
import java.util.*

/**
 * @author LovesAsuna
 **/
interface GroupRecordService : DBService {
    fun groupIsNull(groupID: Long): Boolean

    fun memberIsNull(memberID: Long): Boolean

    fun participationIsNull(memberID: Long, groupID: Long): Boolean

    fun addGroup(groupID: Long, name: String): Boolean

    fun addMember(memberID: Long, name: String): Boolean

    fun addRecord(message: String, time: Date, memberID: Long, groupID: Long): Boolean

    fun addParticipation(memberID: Long, groupID: Long): Boolean

    fun queryUserRecord(memberID: Long): List<MessageEntity>

    fun queryGroupRecord(groupID: Long): List<MessageEntity>

    fun queryUserRecordInGroup(memberID: Long, groupID: Long): List<MessageEntity>
}