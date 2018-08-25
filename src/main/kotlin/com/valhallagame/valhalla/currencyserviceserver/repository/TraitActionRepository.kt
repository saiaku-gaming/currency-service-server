package com.valhallagame.valhalla.actionbarserviceserver.repository

import com.valhallagame.valhalla.actionbarserviceserver.model.TraitAction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TraitActionRepository : JpaRepository<TraitAction, Long> {
    @Query(value = "SELECT ta.* FROM trait_action ta JOIN actionbar_item ai ON(ta.actionbar_item_id = ai.actionbar_item_id) WHERE ai.character_name = :characterName", nativeQuery = true)
    fun findTraitActionByCharacterName(@Param("characterName") characterName: String): Array<TraitAction>
}