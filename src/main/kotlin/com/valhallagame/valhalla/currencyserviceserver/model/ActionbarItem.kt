package com.valhallagame.valhalla.actionbarserviceserver.model

import javax.persistence.*

@Entity
@Table(name = "actionbar_item")
data class ActionbarItem(
        @Id
        @SequenceGenerator(name = "actionbar_item_actionbar_item_id_seq", sequenceName = "actionbar_item_actionbar_item_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actionbar_item_actionbar_item_id_seq")
        @Column(name = "actionbar_item_id")
        val id: Long? = null,

        @Column(name = "character_name")
        val characterName: String,

        @Column(name = "index")
        val index: Int
)
