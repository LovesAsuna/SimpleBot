package com.hyosakura.bot.entity.message

import javax.persistence.*

/**
 * @author LovesAsuna
 */
@Entity
@Table(name = "members")
class MemberEntity(
    @Id
    var id: Long? = null,

    @Column(name = "name")
    @Basic
    var name: String? = null,

    @OneToMany(targetEntity = ParticipationEntity::class)
    @JoinColumn(name = "member_id")
    var participation: MutableSet<ParticipationEntity>? = null,

    @OneToMany(targetEntity = MessageEntity::class)
    @JoinColumn(name = "member_id")
    var messages: MutableSet<MessageEntity>? = null
) {
    override fun toString(): String = "$name($id)"
}