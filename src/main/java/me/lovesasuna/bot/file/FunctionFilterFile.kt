package me.lovesasuna.bot.file

import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.data.FilterData
import me.lovesasuna.bot.util.BasicUtil
import me.lovesasuna.bot.util.annotations.Filter
import me.lovesasuna.bot.util.annotations.processors.FilterProcessorHandler
import me.lovesasuna.bot.util.interfaces.file.FileManipulate

object FunctionFilterFile : AbstractFile() {
    override val file = BasicUtil.getLocation("filter.json")
    lateinit var data: FilterData

    override fun writeDefault() {
        val list = ArrayList<MutableMap<String, MutableList<Long>>>()
        val classes = FilterProcessorHandler.getClasses("me.lovesasuna.bot.function", Filter::class.java)
        classes.forEach {
            val map = hashMapOf<String, MutableList<Long>>()
            map[it.simpleName] = mutableListOf<Long>(1)
            list.add(map)
        }
        val data = FilterData(list)
        if (!file.exists()) {
            BotData.objectMapper!!.writerWithDefaultPrettyPrinter().writeValue(file, data)
        }
        readValue()
    }

    override fun writeValue() {
        return
    }

    override fun readValue() {
        data = BotData.objectMapper!!.readValue(file, FilterData::class.java)
    }

    fun filter(function: String, groupID: Long): Boolean {
        var filter = false
        data.filter.forEach {
            filter = it[function]!!.contains(groupID)
        }
        return filter
    }
}