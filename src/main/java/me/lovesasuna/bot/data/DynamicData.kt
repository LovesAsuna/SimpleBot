package me.lovesasuna.bot.data

import java.io.Serializable
import java.util.HashMap
import java.util.HashSet

data class DynamicData(var upSet: HashSet<Int>, var subscribeMap: HashMap<Int, HashSet<Long>>, var dynamicMap: HashMap<Int, String>, var time: String, var intercept: Boolean) : Serializable
