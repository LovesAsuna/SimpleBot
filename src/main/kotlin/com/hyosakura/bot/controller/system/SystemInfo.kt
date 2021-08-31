package com.hyosakura.bot.controller.system

import kotlinx.coroutines.delay
import com.hyosakura.bot.Main
import me.lovesasuna.bot.util.registerDefaultPermission
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.text.DecimalFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

/**
 * @author LovesAsuna
 **/
object SystemInfo : SimpleCommand(
    owner = com.hyosakura.bot.Main,
    primaryName = "统计",
    "运行状态",
    description = "系统信息",
    parentPermission = registerDefaultPermission()
) {
    private val systemInfo = SystemInfo()
    val startTime = LocalDateTime.now()

    @Handler
    suspend fun CommandSender.handle() {
        val processor = systemInfo.hardware.processor
        val prevTicks = processor.systemCpuLoadTicks
        // 睡眠1s
        try {
            delay(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val ticks = processor.systemCpuLoadTicks
        val nice = ticks[CentralProcessor.TickType.NICE.index] - prevTicks[CentralProcessor.TickType.NICE.index]
        val irq = ticks[CentralProcessor.TickType.IRQ.index] - prevTicks[CentralProcessor.TickType.IRQ.index]
        val softirq =
            ticks[CentralProcessor.TickType.SOFTIRQ.index] - prevTicks[CentralProcessor.TickType.SOFTIRQ.index]
        val steal = ticks[CentralProcessor.TickType.STEAL.index] - prevTicks[CentralProcessor.TickType.STEAL.index]
        val cSys = ticks[CentralProcessor.TickType.SYSTEM.index] - prevTicks[CentralProcessor.TickType.SYSTEM.index]
        val user = ticks[CentralProcessor.TickType.USER.index] - prevTicks[CentralProcessor.TickType.USER.index]
        val iowait = ticks[CentralProcessor.TickType.IOWAIT.index] - prevTicks[CentralProcessor.TickType.IOWAIT.index]
        val idle = ticks[CentralProcessor.TickType.IDLE.index] - prevTicks[CentralProcessor.TickType.IDLE.index]
        val totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal
        val memory = systemInfo.hardware.memory
        //总内存
        val totalByte = memory.total
        //剩余
        val acaliableByte = memory.available
        val props: Properties = System.getProperties()
        //系统名称
        val osName: String = props.getProperty("os.name")
        //架构名称
        val osArch: String = props.getProperty("os.arch")
        val runtime = Runtime.getRuntime()
        //jvm总内存
        val jvmTotalMemoryByte = runtime.totalMemory()
        //jvm最大可申请
        // val jvmMaxMoryByte = runtime.maxMemory()
        //空闲空间
        val freeMemoryByte = runtime.freeMemory()
        //jdk版本
        val jdkVersion: String = props.getProperty("java.version")
        //jdk路径
        // val jdkHome: String = props.getProperty("java.home")
        val nowTime = LocalDateTime.now()
        val duration: Duration = Duration.between(startTime, nowTime)
        val days: Long = duration.toDays()
        val hours: Long = duration.toHours() % 24
        val minutes: Long = duration.toMinutes() % 60
        val ss = days.toString() + "天" + hours + "小时" + minutes + "分钟"
        val info = """
        程序运行时长：$ss
        cpu核数：${processor.logicalProcessorCount}
        cpu当前使用率：${DecimalFormat("#.##%").format(1.0 - idle * 1.0 / totalCpu)}
        总内存：${formatByte(totalByte)}
        已使用内存：${formatByte(totalByte - acaliableByte)}
        操作系统：$osName
        系统架构：$osArch
        jvm内存总量：${formatByte(jvmTotalMemoryByte)}
        jvm已使用内存：${formatByte(jvmTotalMemoryByte - freeMemoryByte)}
        java版本：$jdkVersion
        """.trimIndent()
        sendMessage(info)
    }

    private fun formatByte(byteNumber: Long): String? {
        //换算单位
        val format = 1024.0
        val kbNumber = byteNumber / format
        if (kbNumber < format) {
            return DecimalFormat("#.##KB").format(kbNumber)
        }
        val mbNumber = kbNumber / format
        if (mbNumber < format) {
            return DecimalFormat("#.##MB").format(mbNumber)
        }
        val gbNumber = mbNumber / format
        if (gbNumber < format) {
            return DecimalFormat("#.##GB").format(gbNumber)
        }
        val tbNumber = gbNumber / format
        return DecimalFormat("#.##TB").format(tbNumber)
    }
}