package me.lovesasuna.bot.entity.message

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

    @Column(name = "nickname")
    @Basic
    var nickname: String? = null,
) {
    override fun toString(): String {
        return "群员 $memberID 在群 $groupID 的昵称为 $nickname"
    }
}


data class GroupAndMember(
    var groupID: Long? = null,
    var memberID: Long? = null,
) : Serializable
