package me.lovesasuna.bot.entity.dynamic

import javax.persistence.*

/**
 * @author LovesAsuna
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