package com.valhallagame.valhalla.currencyserviceserver.controller

import com.fasterxml.jackson.databind.JsonNode
import com.valhallagame.common.JS
import com.valhallagame.currencyserviceclient.message.LockCurrencyParameter
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.validation.Valid

@Controller
@RequestMapping(path = ["/v1/locked-currency"])
class LockedCurrencyController {
    @Autowired
    private lateinit var lockedCurrencyService: LockedCurrencyService

    @PostMapping(path = ["/lock-currency"])
    @ResponseBody
    fun lockCurrency(@Valid @RequestBody input: LockCurrencyParameter): ResponseEntity<JsonNode> {
        return try {
            val lockedCurrency = lockedCurrencyService.lockCurrency(input.characterName, input.amount, input.currencyType)
            JS.message(HttpStatus.OK, lockedCurrency)
        } catch (e: InsufficientCurrencyException) {
            JS.message(HttpStatus.BAD_REQUEST, e.message)
        } catch(e: CurrencyMissingException) {
            JS.message(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}