package com.valhallagame.valhalla.actionbarserviceserver.service

import com.valhallagame.valhalla.actionbarserviceserver.model.ActionbarItem
import com.valhallagame.valhalla.actionbarserviceserver.repository.ActionbarItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ActionbarItemService
    @Autowired
    constructor(
            private val actionbarItemRepository: ActionbarItemRepository
    ){

    fun saveActionbarItem(actionbarItem: ActionbarItem) = actionbarItemRepository.save(actionbarItem)
    fun deleteActionbarItem(actionbarItem: ActionbarItem) = actionbarItemRepository.delete(actionbarItem)
    fun deleteActionbarItemByIndex(characterName: String, index: Int) = actionbarItemRepository.deleteActionbarItemByCharacterNameAndIndex(characterName, index)
}