package com.hyosakura.bot.controller.misc.bangumi

import kotlinx.coroutines.flow.Flow
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain

/**
 * @author LovesAsuna
 **/
interface BangumiFunction {
    val type: BangumiType

    suspend fun getBangumi(contact: Contact, keyword: String): Flow<MessageChain>

    enum class BangumiType(val id: Int, val typeName: String) {
        BOOK(1, "书籍"),
        ANIMATION(2, "动画"),
        MUSIC(3, "音乐"),
        GAME(4, "游戏")
    }
}
