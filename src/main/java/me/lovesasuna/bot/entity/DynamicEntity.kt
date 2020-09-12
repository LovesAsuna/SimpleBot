package me.lovesasuna.bot.entity

import javax.persistence.*

@Entity
@Table(name = "DYNAMIC")
data class DynamicEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        @Column(columnDefinition = "text")
        var upSet: String = "[]",
/*HashMap<Int, HashSet<Long>>*/
        @Column(columnDefinition = "text")
        var subscribeMap: String = "{}",
/*HashMap<Int, String>*/
        @Lob
        @Column(columnDefinition = "text")
        var dynamicMap: String = "{}",
        var time: String? = null,
        var intercept: Boolean = false) {

}
