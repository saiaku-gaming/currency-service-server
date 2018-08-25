package com.valhallagame.valhalla.actionbarserviceserver.model

import javax.persistence.*

@Entity
@Table(name = "item_action")
data class ItemAction(
        @Id
        @SequenceGenerator(name = "item_action_item_action_id_seq", sequenceName = "item_action_item_action_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_action_item_action_id_seq")
        @Column(name = "item_action_id")
        val id: Long? = null,

        @OneToOne
        @JoinColumn(name = "actionbar_item_id")
        val actionbarItem: ActionbarItem,

        @Column(name = "item_name")
        val itemName: String
)