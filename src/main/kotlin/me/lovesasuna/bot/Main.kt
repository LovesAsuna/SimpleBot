package me.lovesasuna.bot

import com.google.auto.service.AutoService
import me.lovesasuna.bot.util.plugin.PluginScheduler
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import java.lang.management.ManagementFactory

/**
 * @author LovesAsuna
 */
@AutoService(KotlinPlugin::class)
object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "me.lovesasuna.bot",
        version = "1.0",
        name = "Mirai-Bot"
    )
) {
    val scheduler = PluginScheduler()
    override fun onEnable() {
        logger.info("[Mirai-Bot] 插件已成功启用!")
        val runtimeMX = ManagementFactory.getRuntimeMXBean()
        val osMX = ManagementFactory.getOperatingSystemMXBean()
        if (runtimeMX != null && osMX != null) {
            val javaInfo = "Java " + runtimeMX.specVersion + " (" + runtimeMX.vmName + " " + runtimeMX.vmVersion + ")"
            val osInfo = "Host: " + osMX.name + " " + osMX.version + " (" + osMX.arch + ")"
            logger.info("System Info: $javaInfo $osInfo")
        } else {
            logger.info("Unable to read system info")
        }
    }
}
