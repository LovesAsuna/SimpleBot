package me.lovesasuna.bot.entity.database.message

import javax.persistence.*

/**
 * @author LovesAsuna
 */
@Entity
@Table(name = "groups")
data class GroupEntity(
    @Id
    var id: Long? = null,

    @Column(name = "name")
    @Basic
    var name: String? = null,

    @OneToMany(targetEntity = ParticipationEntity::class)
    @JoinColumn(name = "group_id")
    var participation: MutableSet<ParticipationEntity>? = null,

    @OneToMany(targetEntity = MessageEntity::class)
    @JoinColumn(name = "group_id")
    var messages: MutableSet<MessageEntity>? = null
)