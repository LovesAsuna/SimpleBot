package me.lovesasuna.bot.util.interfaces.dao

interface HibernateDao<T> {
    fun queryEntity(queryString: String, resultType: Class<T>, vararg prams: Any): List<T>

    fun <F>queryField(queryString: String, resultType: Class<F>, vararg prams: Any): List<F>

    fun update(queryString: String,  vararg prams: Any) : Int

    fun save(`object` : Any)
}