package me.lovesasuna.bot.entity.database.message

import javax.persistence.*

/**
 * @author LovesAsuna
 */
@Entity
@Table(name = "groups")
data class GroupEntity(
    @Id
    var id: Int? = null,
    @Column(name = "NAME")
    @Basic
    var name: String? = null,
    @ManyToMany
    @JoinTable(
        name = "participation",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "member_id")]
    )
    var members: MutableSet<MemberEntity>? = null
)