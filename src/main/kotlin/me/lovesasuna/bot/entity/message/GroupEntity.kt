package me.lovesasuna.bot.entity.message

import javax.persistence.*

/**
 * @author LovesAsuna
 */
@Entity
@Table(name = "groups")
class GroupEntity(
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
) {
    override fun toString(): String {
        return "GroupEntity(id=$id, name=$name, participation=$participation)"
    }
}