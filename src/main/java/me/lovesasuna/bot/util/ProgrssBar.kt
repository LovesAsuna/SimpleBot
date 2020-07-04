package me.lovesasuna.bot.util

import kotlinx.coroutines.delay

class ProgressBar {
    var index = 0.0
    private var finish: String = ""
    private var unFinish: String = ""

    // 进度条粒度
    private val PROGRESS_SIZE = 50
    private fun getNChar(num: Int, ch: Char): String {
        val builder = StringBuilder()
        for (i in 0 until num) {
            builder.append(ch)
        }
        return builder.toString()
    }

    @Throws(InterruptedException::class)
    suspend fun printProgress(interval: Long) {
        print("Progress:")
        finish = getNChar(0, '█')
        unFinish = getNChar(PROGRESS_SIZE, '─')
        var target = String.format(" %5.2f %%[%s%s]", index, finish, unFinish)
        print(target)
        while (index <= 100) {
            finish = getNChar(index.toInt(), '█')
            unFinish = getNChar(PROGRESS_SIZE - index.toInt(), '─')
            printTarget(index * 2)
            delay(interval)
        }
        printTarget(100.0, getNChar(PROGRESS_SIZE, '█'), getNChar(0, '─'))
        println()
    }

    private fun printTarget(progress: Double, finished: String = finish, unFinished: String = unFinish) {
        val target = String.format(" %5.2f %%├%s%s┤", progress, finished, unFinished)
        print(getNChar(PROGRESS_SIZE + 10, '\b'))
        print(target)
    }
}