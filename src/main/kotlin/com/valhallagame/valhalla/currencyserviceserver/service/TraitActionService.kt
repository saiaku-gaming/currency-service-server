package com.valhallagame.valhalla.actionbarserviceserver.service

import com.valhallagame.valhalla.actionbarserviceserver.model.TraitAction
import com.valhallagame.valhalla.actionbarserviceserver.repository.TraitActionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TraitActionService
    @Autowired
    constructor(
            private val traitActionRepository: TraitActionRepository
    ){
    fun saveTraitAction(traitAction: TraitAction) = traitActionRepository.save(traitAction)
    fun deleteTraitAction(traitAction: TraitAction) = traitActionRepository.delete(traitAction)
    fun getTraitActions(characterName: String) = traitActionRepository.findTraitActionByCharacterName(characterName)
}