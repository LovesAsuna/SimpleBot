package me.lovesasuna.bot.util.exceptions

/**
 * @author LovesAsuna
 **/
class MessageTypeNotSingeException : Exception {
    constructor(cause: Throwable) : super(cause)
    constructor() : super("消息序列非单个，请使用List获取具体位置的消息")
}