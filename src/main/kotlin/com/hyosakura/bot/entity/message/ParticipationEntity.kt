package com.hyosakura.bot.entity.message

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "participation")
@IdClass(GroupAndMember::class)
class ParticipationEntity(
    @Id
    @Column(name = "group_id")
    var groupID: Long? = null,

    @Id
    @Column(name = "member_id")
    var memberID: Long? = null,
)


data class GroupAndMember(
    var groupID: Long? = null,
    var memberID: Long? = null,
) : Serializable
