package com.valhallagame.valhalla.actionbarserviceserver.service

import com.valhallagame.valhalla.actionbarserviceserver.model.ItemAction
import com.valhallagame.valhalla.actionbarserviceserver.repository.ItemActionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ItemActionService
    @Autowired
    constructor(
        private val itemActionRepository: ItemActionRepository
    ){

    fun saveItemAction(itemAction: ItemAction) = itemActionRepository.save(itemAction)
    fun deleteItemAction(itemAction: ItemAction) = itemActionRepository.delete(itemAction)
    fun getItemActions(characterName: String) = itemActionRepository.findItemActionByCharacterName(characterName)
}