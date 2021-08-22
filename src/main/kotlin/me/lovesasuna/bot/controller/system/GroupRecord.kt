package me.lovesasuna.bot.controller.system

import me.lovesasuna.bot.controller.FunctionListener
import me.lovesasuna.bot.data.MessageBox
import me.lovesasuna.bot.service.GroupRecordService
import me.lovesasuna.bot.service.impl.GroupRecordImpl
import net.mamoe.mirai.contact.Group
import java.util.*

class GroupRecord : FunctionListener {
    private val recordService: GroupRecordService = GroupRecordImpl
    private var error = false

    override suspend fun execute(box: MessageBox): Boolean {
        val event = box.event
        val member = event.sender
        val group = event.subject as Group
        try {
            if (recordService.groupIsNull(group.id)) {
                recordService.addGroup(group.id, group.name)
            }
            if (recordService.memberIsNull(member.id)) {
                recordService.addMember(member.id, member.nick)
            }
            if (recordService.participationIsNull(member.id, group.id)) {
                recordService.addParticipation(group.id, member.id, event.senderName)
            } else {
                recordService.updateParticipationNickName(group.id, member.id, event.senderName)
            }
            recordService.addRecord(event.message.toString(), Date(), member.id, group.id)
        } catch (e: Exception) {
            e.printStackTrace()
            if (!error) {
                box.reply("聊天记录入库时发生错误:\n$e")
                error = true
            }
            return false
        }
        return true
    }
}