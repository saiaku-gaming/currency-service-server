package com.valhallagame.valhalla.actionbarserviceserver.repository

import com.valhallagame.valhalla.actionbarserviceserver.model.ItemAction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ItemActionRepository : JpaRepository<ItemAction, Long> {
    @Query(value = "SELECT ia.* FROM item_action ia JOIN actionbar_item ai ON (ia.actionbar_item_id = ai.actionbar_item_id) WHERE ai.character_name = :characterName", nativeQuery = true)
    fun findItemActionByCharacterName(@Param("characterName") characterName: String): Array<ItemAction>
}