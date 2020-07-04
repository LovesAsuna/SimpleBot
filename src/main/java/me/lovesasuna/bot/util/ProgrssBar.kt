package me.lovesasuna.bot.util

import kotlinx.coroutines.delay

class ProgressBar(val PROGRESS_SIZE: Int = 50) {
    var index = 0.0
    private var finish: String = ""
    private var unFinish: String = ""

    private fun getNChar(num: Int, ch: Char): String {
        val builder = StringBuilder()
        for (i in 0 until num) {
            builder.append(ch)
        }
        return builder.toString()
    }

    suspend fun printProgress(interval: Long) {
        print("Progress:")
        finish = getNChar(0, '█')
        unFinish = getNChar(PROGRESS_SIZE, '─')
        printTarget(0.0) {}
        while (index < PROGRESS_SIZE) {
            finish = getNChar(index.toInt(), '█')
            unFinish = getNChar(PROGRESS_SIZE - index.toInt(), '─')
            printTarget(index / PROGRESS_SIZE) {
                print(getNChar(PROGRESS_SIZE + 10, '\b'))
            }
            delay(interval)
        }
        printTarget(1.0, getNChar(PROGRESS_SIZE, '█'), getNChar(0, '─')) {
            print(getNChar(PROGRESS_SIZE + 2, '\b'))
        }
        println()
    }

    private fun printTarget(progress: Double, finished: String = finish, unFinished: String = unFinish, clear: () -> Unit) {
        val target = String.format(" %5.2f %%├%s%s┤", progress * 100, finished, unFinished)
        clear.invoke()
        print(target)
    }
}