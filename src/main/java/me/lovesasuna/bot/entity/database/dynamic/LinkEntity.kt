package me.lovesasuna.bot.entity.database.dynamic

import javax.persistence.*

/**
 * @author LovesAsuna
 * @date 2020/9/12 18:03
 **/

@Entity
@Table(name = "link")
data class LinkEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(name = "groups")
        var groupID: Long? = null,
        @Column(name = "ups")
        var upID: Long? = null
)