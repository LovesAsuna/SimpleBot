package me.lovesasuna.bot.data

import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.Lob

data class DynamicData(
        @Id
        var id: Int? = null,
        @Lob
        var upSet: String = "[]",
        var subscribeMap: HashMap<Int, HashSet<Long>>,
        var dynamicMap: HashMap<Int, String>,
        @Lob
        var time: String,
        var intercept: Boolean) {

}
