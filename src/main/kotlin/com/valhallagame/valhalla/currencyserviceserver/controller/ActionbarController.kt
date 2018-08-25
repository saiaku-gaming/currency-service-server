package com.valhallagame.valhalla.actionbarserviceserver.controller

import com.fasterxml.jackson.databind.JsonNode
import com.valhallagame.actionbarserviceclient.message.GetActionbarParameter
import com.valhallagame.actionbarserviceclient.message.RemoveActionbarActionParameter
import com.valhallagame.actionbarserviceclient.message.SetActionbarItemActionParameter
import com.valhallagame.actionbarserviceclient.message.SetActionbarTraitActionParameter
import com.valhallagame.common.JS
import com.valhallagame.valhalla.actionbarserviceserver.model.ActionbarItem
import com.valhallagame.valhalla.actionbarserviceserver.model.ItemAction
import com.valhallagame.valhalla.actionbarserviceserver.model.TraitAction
import com.valhallagame.valhalla.actionbarserviceserver.service.ActionbarItemService
import com.valhallagame.valhalla.actionbarserviceserver.service.ItemActionService
import com.valhallagame.valhalla.actionbarserviceserver.service.TraitActionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import javax.validation.Valid

@Controller
@RequestMapping(path = ["/v1/actionbar"])
class ActionbarController
    @Autowired
    constructor(
        private val actionbarItemService: ActionbarItemService,
        private val traitActionService: TraitActionService,
        private val itemActionService: ItemActionService
    ){

    @RequestMapping(path = ["/set-actionbar-trait-action"], method = [RequestMethod.POST])
    @ResponseBody
    fun setActionbarTraitAction(@Valid @RequestBody input: SetActionbarTraitActionParameter): ResponseEntity<JsonNode> {
        actionbarItemService.deleteActionbarItemByIndex(input.characterName, input.index)
        val actionbarItem = actionbarItemService.saveActionbarItem(ActionbarItem(characterName = input.characterName, index = input.index))

        traitActionService.saveTraitAction(
                TraitAction(
                        traitName = input.traitName,
                        actionbarItem = actionbarItem
                )
        )

        return JS.message(HttpStatus.OK, "Actionbar trait action set")
    }

    @RequestMapping(path = ["/set-actionbar-item-action"], method = [RequestMethod.POST])
    @ResponseBody
    fun setActionbarItemAction(@Valid @RequestBody input: SetActionbarItemActionParameter): ResponseEntity<JsonNode> {
        actionbarItemService.deleteActionbarItemByIndex(input.characterName, input.index)
        val actionbarItem = actionbarItemService.saveActionbarItem(ActionbarItem(characterName = input.characterName, index = input.index))

        itemActionService.saveItemAction(
                ItemAction(
                        itemName = input.itemName,
                        actionbarItem = actionbarItem
                )
        )

        return JS.message(HttpStatus.OK, "Actionbar item action set")
    }

    @RequestMapping(path = ["/remove-actionbar-action"], method = [RequestMethod.POST])
    @ResponseBody
    fun removeActionbarAction(@Valid @RequestBody input: RemoveActionbarActionParameter): ResponseEntity<JsonNode> {
        actionbarItemService.deleteActionbarItemByIndex(input.characterName, input.index)

        return JS.message(HttpStatus.OK, "Actionbar item action set")
    }

    @RequestMapping(path = ["/get-actionbar"], method = [RequestMethod.POST])
    @ResponseBody
    fun getActionbar(@Valid @RequestBody input: GetActionbarParameter): ResponseEntity<JsonNode> {
        val itemActions = itemActionService.getItemActions(input.characterName)
        val traitActions = traitActionService.getTraitActions(input.characterName)

        val data = mapOf(Pair<String, Any>("itemActions", itemActions), Pair<String, Any>("traitActions", traitActions))

        return JS.message(HttpStatus.OK, data)
    }
}