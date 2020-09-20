package me.lovesasuna.bot.entity

import javax.persistence.*

@Entity
@Table(name = "notice")
data class NoticeEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(name = "groups")
        var groupID: Long? = null,
        @Column(name = "targets")
        var targetID: Long? = null,
        @Lob
        var message: String? = null
)