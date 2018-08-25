package com.valhallagame.valhalla.actionbarserviceserver.model

import javax.persistence.*

@Entity
@Table(name = "trait_action")
data class TraitAction(
        @Id
        @SequenceGenerator(name = "trait_action_trait_action_id_seq", sequenceName = "trait_action_trait_action_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trait_action_trait_action_id_seq")
        @Column(name = "trait_action_id")
        val id: Long? = null,

        @OneToOne
        @JoinColumn(name = "actionbar_item_id")
        val actionbarItem: ActionbarItem,

        @Column(name = "trait_name")
        val traitName: String
)