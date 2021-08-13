package me.lovesasuna.bot.entity.database.message

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "participation")
@IdClass(GroupAndMember::class)
data class ParticipationEntity(
    @Id
    @Column(name = "group_id")
    var groupID: Long? = null,

    @Id
    @Column(name = "member_id")
    var memberID: Long? = null,

    @Column(name = "nickname")
    @Basic
    var nickname: String? = null,
)


private data class GroupAndMember(
    var groupID: Long? = null,
    var memberID: Long? = null,
) : Serializable
