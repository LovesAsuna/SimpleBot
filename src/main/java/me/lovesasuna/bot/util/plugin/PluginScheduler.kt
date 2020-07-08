package me.lovesasuna.bot.util.plugin

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PluginScheduler(override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext) : CoroutineScope {


    class RepeatTaskReceipt(@Volatile var cancelled: Boolean = false)

    /**
     * 新增一个 Repeat Task (定时任务)
     *
     * 这个 Runnable 会被每 [intervalMs] 调用一次(不包含 [runnable] 执行时间)
     *
     * 使用返回的 [RepeatTaskReceipt], 可以取消这个定时任务
     */
    fun repeat(runnable: Runnable, intervalMs: Long): RepeatTaskReceipt {
        val receipt = RepeatTaskReceipt()

        this.launch {
            while (isActive && (!receipt.cancelled)) {
                withContext(Dispatchers.IO) {
                    runnable.run()
                }
                delay(intervalMs)
            }
        }

        return receipt
    }

    /**
     * 新增一个 Delay Task (延迟任务)
     *
     * 在延迟 [delayMs] 后执行 [runnable]
     *
     * 作为 Java 使用者, 你要注意可见性, 原子性
     */
    fun delay(runnable: Runnable, delayMs: Long) {
        this.launch {
            delay(delayMs)
            withContext(Dispatchers.IO) {
                runnable.run()
            }
        }
    }

    /**
     * 异步执行一个任务, 没有返回
     */
    fun async(runnable: CoroutineScope) {
        this.launch {
            withContext(Dispatchers.IO) {
                runnable
            }
        }
    }

}

