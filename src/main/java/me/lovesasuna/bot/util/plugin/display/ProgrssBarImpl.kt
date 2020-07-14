package me.lovesasuna.bot.util.plugin.display

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.lovesasuna.bot.util.interfaces.ProgressBar

class ProgressBarImpl(val PROGRESS_SIZE: Int = 50) : ProgressBar {
    var index = 0.0
    private var interval = 100L
    private var finish: String = ""
    private var unFinish: String = ""

    private fun getNChar(num: Int, ch: Char): String {
        val builder = StringBuilder()
        for (i in 0 until num) {
            builder.append(ch)
        }
        return builder.toString()
    }

    override fun setInterval(interval: Long) {
        this.interval = interval
    }

    override suspend fun printWithInterval(interval: Long) {
        print("Progress:")
        finish = getNChar(0, '=')
        unFinish = getNChar(PROGRESS_SIZE, '─')
        printTarget(0.0) {}
        while (index < PROGRESS_SIZE) {
            finish = getNChar(index.toInt(), '=')
            unFinish = getNChar(PROGRESS_SIZE - index.toInt(), ' ')
            printTarget(index / PROGRESS_SIZE) {
                print(getNChar(PROGRESS_SIZE + 10, '\b'))
            }
            delay(interval)
        }
        printTarget(1.0, getNChar(PROGRESS_SIZE, '='), getNChar(0, ' ')) {
            print(getNChar(PROGRESS_SIZE + 10, '\b'))
        }
        println()
    }

    private fun printTarget(progress: Double, finished: String = finish, unFinished: String = unFinish, clear: () -> Unit) {
        val target = String.format(" %5.2f %%├%s%s┤", progress * 100, finished, unFinished)
        clear.invoke()
        print(target)
    }

    override fun print() {
        runBlocking {
            printWithInterval(interval)
        }
    }
}