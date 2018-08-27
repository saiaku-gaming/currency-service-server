package com.valhallagame.valhalla.currencyserviceserver.controller

import com.fasterxml.jackson.databind.JsonNode
import com.valhallagame.common.JS
import com.valhallagame.currencyserviceclient.message.AddCurrencyParameter
import com.valhallagame.currencyserviceclient.message.GetCurrenciesParameter
import com.valhallagame.currencyserviceclient.message.GetCurrencyParameter
import com.valhallagame.currencyserviceclient.message.SubtractCurrencyParameter
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.service.CurrencyService
import org.slf4j.LoggerFactory
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
@RequestMapping(path = ["/v1/currency"])
class CurrencyController {
    private val logger = LoggerFactory.getLogger(CurrencyController::class.java)

    @Autowired
    private lateinit var currencyService: CurrencyService

    @ResponseBody
    @PostMapping("/add-currency")
    fun addCurrency(@Valid @RequestBody input: AddCurrencyParameter): ResponseEntity<JsonNode> {
        val addedCurrency = currencyService.addCurrency(input.characterName, input.currencyType, input.amount)

        return JS.message(HttpStatus.OK, addedCurrency)
    }

    @ResponseBody
    @PostMapping("/subtract-currency")
    fun subtractCurrency(@Valid @RequestBody input: SubtractCurrencyParameter): ResponseEntity<JsonNode> {
        return try {
            val removedCurrency = currencyService.subtractCurrency(input.characterName, input.currencyType, input.amount)
            JS.message(HttpStatus.OK, removedCurrency)
        } catch (e: CurrencyMissingException) {
            logger.error("Error while subtracting currency with: $input", e)
            JS.message(HttpStatus.NOT_FOUND, "Unable to find currency ${input.currencyType.name} for ${input.characterName}")
        } catch (e: InsufficientCurrencyException) {
            logger.error("Error while subtracting currency with: $input", e)
            JS.message(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @ResponseBody
    @PostMapping("/get-currency")
    fun getCurrency(@Valid @RequestBody input: GetCurrencyParameter): ResponseEntity<JsonNode> {
        return try {
            val currency = currencyService.getCurrency(input.characterName, input.currencyType)
            JS.message(HttpStatus.OK, currency)
        } catch (e: CurrencyMissingException) {
            logger.error("Error while getting currency with: $input", e)
            JS.message(HttpStatus.NOT_FOUND, "Unable to find currency ${input.currencyType.name} for ${input.characterName}")
        }
    }

    @ResponseBody
    @PostMapping("/get-currencies")
    fun getCurrencies(@Valid @RequestBody input: GetCurrenciesParameter): ResponseEntity<JsonNode> = JS.message(HttpStatus.OK, currencyService.getCurrencies(input.characterName))
}