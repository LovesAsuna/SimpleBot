package me.lovesasuna.bot.function

import me.lovesasuna.bot.util.Listener
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChain
import java.util.*

/**
 * @author LovesAsuna
 * @date 2020/4/22 23:50
 */
class RepeatDetect : Listener {
    private val maps: MutableMap<Long, MutableList<MessageChain>> = HashMap()
    override suspend fun execute(event: MessageEvent, message: String, image: Image?, face: Face?): Boolean {
        val groupID = (event as GroupMessageEvent).group.id
        if (groupID == 558529644L) {
            return false
        }
        maps.putIfAbsent(groupID, ArrayList())
        val messageList = maps[groupID]!!

        if (messageList.size >= 3) {
            messageList.removeAt(0)
        }

        operate(event, messageList)

        if (messageList.size < 3) {
            return false
        }

        if (isRepeat(messageList)) {
            val builder = StringBuilder()
            val stringList = listOf("你", "群", "天", "天", "复", "读")
            val random = Random()
            val i = random.nextInt(3)
            when (i ) {
                0 -> {
                    Collections.shuffle(stringList)
                    stringList.forEach { str: String? -> builder.append(str) }
                    event.reply(builder.toString())
                }
                1 -> event.reply("你群天天复读")
                2 -> {
                    event.reply(messageList[2])
                }
            }
            messageList.clear()
        }
        return true
    }

    private fun operate(event: MessageEvent, messageList: MutableList<MessageChain>) {
        messageList.add(event.message)
    }

    private fun isRepeat(messageList: MutableList<MessageChain>): Boolean {
        val first = messageList.first()
        val second = messageList[1]
        val third = messageList[2]
        if (first.contentEquals(second) && second.contentEquals(third)) {
            return true
        }
        return false
    }
}