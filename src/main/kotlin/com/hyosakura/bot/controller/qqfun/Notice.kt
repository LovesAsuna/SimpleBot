package com.hyosakura.bot.controller.qqfun

import com.hyosakura.bot.Main
import com.hyosakura.bot.controller.FunctionListener
import com.hyosakura.bot.data.MessageBox
import com.hyosakura.bot.service.NoticeService
import com.hyosakura.bot.service.impl.NoticeServiceImpl
import com.hyosakura.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import java.util.*

class Notice : FunctionListener, SimpleCommand(
    owner = Main,
    primaryName = "notice",
    description = "提醒",
    parentPermission = registerDefaultPermission()
) {
    private val calendar = Calendar.getInstance()

    override suspend fun execute(box: MessageBox): Boolean {
        val groupID = box.group!!.id
        val senderID = box.sender.id
        noticeService.getMatchMessage(groupID, senderID)?.let {
            box.reply(it)
            noticeService.removeNotice(groupID, senderID)
            return true
        }
        return false
    }

    @Handler
    suspend fun CommandSender.handle(target: Member, message: MessageChain) {
        noticeService.addNotice(
            this.subject!!.id,
            target.id,
            At(target.id) + PlainText(
                "\n${this.user!!.nick}(${this.user!!.id}) ${getTime(Calendar.HOUR_OF_DAY)}:${getTime(Calendar.MINUTE)}:${
                    getTime(Calendar.SECOND)
                }\n"
            ) + message
        )
        sendMessage(At(this.user!!.id) + "此留言将在该用户下次说话时发送！")
    }

    private fun getTime(filed: Int): Int {
        return calendar.get(filed)
    }

    companion object {
        var noticeService: NoticeService = NoticeServiceImpl
    }
}