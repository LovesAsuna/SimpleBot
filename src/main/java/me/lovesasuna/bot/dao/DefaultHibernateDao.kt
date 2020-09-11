package me.lovesasuna.bot.dao

import me.lovesasuna.bot.data.BotData
import me.lovesasuna.bot.util.interfaces.dao.HibernateDao
import org.hibernate.query.Query

/**
 * @author LovesAsuna
 * @date 2020/9/11 21:48
 **/
open class DefaultHibernateDao<T> : HibernateDao<T> {
    private val factory = BotData.HibernateConfig.buildSessionFactory()

    override fun query(queryString: String, resultType: Class<T>, vararg prams: Any): List<T> {
        return setPrams(queryString, resultType, prams).list()
    }

    override fun update(queryString: String, resultType: Class<T>, vararg prams: Any): Int {
        return setPrams(queryString, resultType, prams).executeUpdate()
    }

    private fun setPrams(queryString: String, resultType: Class<T>, vararg prams: Any): Query<T> {
        val session = factory.openSession()
        val query = session.createQuery(queryString, resultType)
        for ((index, pram) in prams.withIndex()) {
            query.setParameter(index, pram)
        }
        return query
    }

    override fun save(`object`: Any) {
        val session = factory.openSession()
        session.save(`object`)
    }
}