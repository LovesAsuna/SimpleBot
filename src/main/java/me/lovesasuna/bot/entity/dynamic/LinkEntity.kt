package me.lovesasuna.bot.entity.dynamic

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
        var groupID: Int? = null,
        @Column(name = "ups")
        var upID: Int? = null
)