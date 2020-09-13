package me.lovesasuna.bot.entity

import javax.persistence.*

@Entity
@Table(name = "keyword")
data class KeyWordEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(name = "groups")
        var groupID: Long? = null,
        var wordRegex: String = "",
        var reply: String = "",
        var chance: Int? = null
)