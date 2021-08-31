package com.hyosakura.bot.entity.game

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "teamspeak")
data class TeamSpeakEntity(
    @EmbeddedId
    var server: Server? = null,

    @Column(name = "username")
    @Basic
    var username: String? = null,

    @Column(name = "password")
    @Basic
    var password: String? = null,

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "groups")
    var groups: MutableSet<Long>? = null

)

@Embeddable
data class Server(
    var host: String? = null,
    var port: Int? = null,
) : Serializable
