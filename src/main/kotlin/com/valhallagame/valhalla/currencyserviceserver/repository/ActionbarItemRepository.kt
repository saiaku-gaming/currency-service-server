package com.valhallagame.valhalla.actionbarserviceserver.repository

import com.valhallagame.valhalla.actionbarserviceserver.model.ActionbarItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface ActionbarItemRepository : JpaRepository<ActionbarItem, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM actionbar_item WHERE character_name = :characterName AND index = :index", nativeQuery = true)
    fun deleteActionbarItemByCharacterNameAndIndex(@Param("characterName") characterName: String, @Param("index") index: Int)
}