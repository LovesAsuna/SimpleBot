package me.lovesasuna.bot.entity.database.dynamic

import javax.persistence.*

@Entity
@Table(name = "dynamic")
data class DynamicEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @Column(name = "ups")
    var upID: Long? = null,

    @Column(name = "dynamicid")
    var dynamicID: String? = null,
)
