package com.valhallagame.valhalla.currencyserviceserver.controller

import com.fasterxml.jackson.databind.JsonNode
import com.valhallagame.common.JS
import com.valhallagame.currencyserviceclient.message.AbortLockedCurrenciesParameter
import com.valhallagame.currencyserviceclient.message.CommitLockedCurrenciesParameter
import com.valhallagame.currencyserviceclient.message.LockCurrencyParameter
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
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
@RequestMapping(path = ["/v1/locked-currency"])
class LockedCurrencyController {
    companion object {
        private val logger = LoggerFactory.getLogger(LockedCurrencyController::class.java)
    }

    @Autowired
    private lateinit var lockedCurrencyService: LockedCurrencyService

    @PostMapping(path = ["/lock-currencies"])
    @ResponseBody
    fun lockCurrencies(@Valid @RequestBody input: LockCurrencyParameter): ResponseEntity<JsonNode> {
        logger.info("Lock Currencies called with {}", input)
        return try {
            val lockedCurrency = lockedCurrencyService.lockCurrencies(input.characterName, input.currencies)
            JS.message(HttpStatus.OK, lockedCurrency)
        } catch (e: InsufficientCurrencyException) {
            JS.message(HttpStatus.BAD_REQUEST, e.message)
        } catch(e: CurrencyMissingException) {
            JS.message(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PostMapping(path = ["/abort-locked-currencies"])
    @ResponseBody
    fun abortLockedCurrencies(@Valid @RequestBody input: AbortLockedCurrenciesParameter): ResponseEntity<JsonNode> {
        logger.info("Abort Locked Currencies called with {}", input)
        lockedCurrencyService.abortLockedCurrencies(input.lockingId)
        return JS.message(HttpStatus.OK, "Currencies aborted")
    }

    @PostMapping(path = ["/commit-locked-currencies"])
    @ResponseBody
    fun commitLockedCurrencies(@Valid @RequestBody input: CommitLockedCurrenciesParameter): ResponseEntity<JsonNode> {
        logger.info("Commit Locked Currencies called with {}", input)
        lockedCurrencyService.commitLockedCurrencies(input.lockingId)
        return JS.message(HttpStatus.OK, "Currencies commited")
    }
}