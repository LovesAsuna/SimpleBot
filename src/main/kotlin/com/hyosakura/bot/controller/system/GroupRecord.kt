package com.hyosakura.bot.controller.system

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.service.GroupRecordService
import com.hyosakura.bot.service.impl.GroupRecordImpl
import net.mamoe.mirai.contact.Group
import java.time.LocalDateTime

class GroupRecord : FunctionListener {
    private val recordService: GroupRecordService = GroupRecordImpl

    override suspend fun execute(box: MessageBox): Boolean {
        val event = box.event
        val member = event.sender
        val group = event.subject as Group
        runCatching {
            recordService.addGroup(group.id, group.name)
            recordService.addMember(member.id, member.nick)
            recordService.addRelation(member.id, group.id)
            recordService.addRecord(event.message.serializeToMiraiCode(), LocalDateTime.now(), member.id, group.id)
        }.onFailure {
            Main.logger.error(it)
            return false
        }
        return true
    }
}