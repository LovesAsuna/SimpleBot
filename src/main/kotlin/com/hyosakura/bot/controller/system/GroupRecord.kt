package com.hyosakura.bot.controller.system

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.service.GroupRecordService
import com.hyosakura.bot.service.impl.GroupRecordImpl
import net.mamoe.mirai.contact.Group
import java.util.*

class GroupRecord : FunctionListener {
    private val recordService: GroupRecordService = GroupRecordImpl

    override suspend fun execute(box: MessageBox): Boolean {
        val event = box.event
        val member = event.sender
        val group = event.subject as Group
        runCatching {
            if (recordService.groupIsNull(group.id)) {
                recordService.addGroup(group.id, group.name)
            }
            if (recordService.memberIsNull(member.id)) {
                recordService.addMember(member.id, member.nick)
            }
            if (recordService.participationIsNull(member.id, group.id)) {
                recordService.addParticipation(member.id, group.id)
            }
            recordService.addRecord(event.message.serializeToMiraiCode(), Date(), member.id, group.id)
        } .onFailure {
            Main.logger.error(it)
            return false
        }
        return true
    }
}