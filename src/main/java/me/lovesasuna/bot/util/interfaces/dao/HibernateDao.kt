package me.lovesasuna.bot.util.interfaces.dao

interface HibernateDao<T> {
    fun query(queryString: String, resultType: Class<T>, vararg prams: Any): List<T>

    fun update(queryString: String, resultType: Class<T>, vararg prams: Any) : Int

    fun save(`object` : Any)
}