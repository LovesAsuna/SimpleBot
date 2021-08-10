package me.lovesasuna.bot.entity.database.message

import javax.persistence.*

/**
 * @author LovesAsuna
 */
@Entity
@Table(name = "members")
data class MemberEntity(
    @Id
    var id: Int? = null,
    @Column(name = "NAME")
    @Basic
    var name: String? = null,
    @ManyToMany
    @JoinTable(
        name = "participation",
        joinColumns = [JoinColumn(name = "member_id")],
        inverseJoinColumns = [JoinColumn(name = "group_id")]
    )
    var groups: MutableSet<GroupEntity>? = null,
)