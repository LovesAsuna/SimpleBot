package me.lovesasuna.bot.util.logger

import me.lovesasuna.bot.Main
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.utils.MiraiLoggerPlatformBase
import net.mamoe.mirai.utils.SimpleLogger

object ContactLogger : MiraiLoggerPlatformBase() {
    private var logLevel: SimpleLogger.LogPriority = SimpleLogger.LogPriority.VERBOSE
    lateinit var contact: Contact
    override val identity: String = "contact-logger"
    override val isEnabled: Boolean = true

    private fun printLog(message: String?, priority: SimpleLogger.LogPriority) {
        if (shouldLog(logLevel, priority)) {
            Main.scheduler.asyncTask {
                message?.apply {
                    contact.sendMessage(message)
                }
                ""
            }
        }
    }

    public override fun verbose0(message: String?): Unit = printLog(message, SimpleLogger.LogPriority.VERBOSE)
    public override fun verbose0(message: String?, e: Throwable?) {
        if (e != null) verbose((message ?: e.toString()) + "\n${e.stackTraceToString()}")
        else verbose(message.toString())
    }

    public override fun info0(message: String?): Unit = printLog(message, SimpleLogger.LogPriority.INFO)
    public override fun info0(message: String?, e: Throwable?) {
        if (e != null) info((message ?: e.toString()) + "\n${e.stackTraceToString()}")
        else info(message.toString())
    }

    public override fun warning0(message: String?): Unit = printLog(message, SimpleLogger.LogPriority.WARNING)
    public override fun warning0(message: String?, e: Throwable?) {
        if (e != null) warning((message ?: e.toString()) + "\n${e.stackTraceToString()}")
        else warning(message.toString())
    }

    public override fun error0(message: String?): Unit = printLog(message, SimpleLogger.LogPriority.ERROR)
    public override fun error0(message: String?, e: Throwable?) {
        if (e != null) error((message ?: e.toString()) + "\n${e.stackTraceToString()}")
        else error(message.toString())
    }

    public override fun debug0(message: String?): Unit = printLog(message, SimpleLogger.LogPriority.DEBUG)
    public override fun debug0(message: String?, e: Throwable?) {
        if (e != null) debug((message ?: e.toString()) + "\n${e.stackTraceToString()}")
        else debug(message.toString())
    }

    fun setLevel(level: String) : SimpleLogger.LogPriority{
        logLevel = SimpleLogger.LogPriority.valueOf(level.uppercase())
        return logLevel
    }

    private fun shouldLog(
        priority: SimpleLogger.LogPriority,
        settings: SimpleLogger.LogPriority
    ): Boolean = settings <= priority
}

fun CommandSender.verbose(message: String) {
    ContactLogger.contact = this.subject!!
    Main.logger.verbose(message)
}

fun CommandSender.debug(message: String) {
    ContactLogger.contact = this.subject!!
    Main.logger.debug(message)
}

fun CommandSender.warning(message: String) {
    ContactLogger.contact = this.subject!!
    Main.logger.warning(message)
}

fun CommandSender.info(message: String) {
    ContactLogger.contact = this.subject!!
    Main.logger.info(message)
}

fun CommandSender.error(message: String) {
    ContactLogger.contact = this.subject!!
    Main.logger.error(message)
}
