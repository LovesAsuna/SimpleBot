package me.lovesasuna.bot.dao

import me.lovesasuna.bot.entity.KeyWordEntity
import org.hibernate.Session

/**
 * @author LovesAsuna
 * @date 2020/9/13 21:02
 **/
class KeyWordDao(override val session: Session) : DefaultHibernateDao<KeyWordEntity>(session) {
    fun checkKeyWordExist(groupID: Long, wordRegex: String): Boolean {
        return queryEntity("from KeyWordEntity where groupID = $groupID and wordRegex = $wordRegex", KeyWordEntity::class.java).isNotEmpty()
    }

    fun checkKeyWordExist(id: Int): Boolean {
        return queryEntity("from KeyWordEntity where id = $id", KeyWordEntity::class.java).isNotEmpty()
    }

    fun addKeyWord(entity: KeyWordEntity) {
        session.saveOrUpdate(entity)
    }

    fun removeKeyWord(id: Int) {
        update("delete from KeyWordEntity where id = $id")
    }

    fun getKeyWordsByGroup(groupID: Long): List<KeyWordEntity> {
        return queryEntity("from KeyWordEntity where groupID = $groupID", KeyWordEntity::class.java)
    }
}