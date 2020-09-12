package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.BotData
import me.lovesasuna.bot.util.interfaces.dao.HibernateDao
import org.hibernate.Session
import org.hibernate.query.Query

/**
 * @author LovesAsuna
 * @date 2020/9/11 21:48
 **/
open class DefaultHibernateDao<T>(open val session: Session) : HibernateDao<T> {

    override fun queryEntity(queryString: String, resultType: Class<T>, vararg prams: Any): List<T> {
        return setPrams(queryString, resultType, *prams).list()
    }

    override fun <F>queryField(queryString: String, resultType: Class<F>, vararg prams: Any): List<F> {
        return setPrams(queryString, resultType, *prams).list()
    }

    override fun update(queryString: String, resultType: Class<T>, vararg prams: Any): Int {
        return setPrams(queryString, resultType, *prams).executeUpdate()
    }

    private fun <T>setPrams(queryString: String, resultType: Class<T>, vararg prams: Any): Query<T> {
        val query = session.createQuery(queryString, resultType)
        for ((index, pram) in prams.withIndex()) {
            query.setParameter(index, pram)
        }
        return query
    }

    override fun save(`object`: Any) {
        session.save(`object`)
    }
}