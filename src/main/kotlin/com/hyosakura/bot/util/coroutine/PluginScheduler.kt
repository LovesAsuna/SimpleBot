package com.hyosakura.bot.util.coroutine

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.CoroutineContext

class PluginScheduler(override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default) :
    CoroutineScope {


    class RepeatTaskReceipt(@Volatile var cancelled: Boolean = false)

    /**
     * 新增一个 Repeat Task (定时任务)
     *
     * 这个 Runnable 会被每 [delay] 调用一次(不包含 [command] 执行时间)
     *
     * 使用返回的 [RepeatTaskReceipt], 可以取消这个定时任务
     */
    fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): Pair<Job, RepeatTaskReceipt> {
        val receipt = RepeatTaskReceipt()
        val job = launch {
            delay(unit.toMillis(initialDelay))
            while (!receipt.cancelled && this.isActive) {
                withContext(Dispatchers.IO) {
                    command.run()
                }
                delay(unit.toMillis(delay))
            }
        }
        return Pair(job, receipt)
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
    fun asyncTask(runnable: suspend () -> Any) {
        this.launch {
            withContext(Dispatchers.IO) {
                runnable.invoke()
            }
        }
    }

    /**
     * 执行一个任务，若超时执行Action
     *
     * @param consumer 执行的任务
     * @param delayMs 超时时间
     * @param notCompletedAction 超时动作
     */
    @Throws(TimeoutException::class)
    suspend fun <R> withTimeOut(consumer: suspend () -> R, delayMs: Long, notCompletedAction: suspend () -> Unit): R? {
        return runCatching {
            withTimeout(delayMs) {
                consumer.invoke()
            }
        }.onFailure {
            notCompletedAction.invoke()
        }.getOrNull()
    }
}

