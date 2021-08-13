package me.lovesasuna.bot.entity.database.message

import java.util.*
import javax.persistence.*

/**
 * @author LovesAsuna
 **/
@Entity
@Table(name = "messages")
data class MessageEntity(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "content")
    @Basic
    var content: String? = null,

    @Column(name = "time")
    var time: Date? = null,

    @ManyToOne(targetEntity = MemberEntity::class)
    @JoinColumn(name = "member_id")
    var member : MemberEntity? = null,

    @ManyToOne(targetEntity = GroupEntity::class)
    @JoinColumn(name = "group_id")
    var group : GroupEntity? = null
)