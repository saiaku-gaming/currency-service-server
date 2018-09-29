package com.valhallagame.valhalla.currencyserviceserver

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.valhallagame.currencyserviceclient.message.AbortLockedCurrenciesParameter
import com.valhallagame.currencyserviceclient.message.CommitLockedCurrenciesParameter
import com.valhallagame.currencyserviceclient.message.LockCurrencyParameter
import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.controller.LockedCurrencyController
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@WebMvcTest(LockedCurrencyController::class)
@ActiveProfiles("test")
class LockedCurrencyControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var lockedCurrencyService: LockedCurrencyService

    private val objectMapper = ObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(ParameterNamesModule())
            .registerModule(JavaTimeModule())

    @Test
    fun lockCurrency() {
        val input = LockCurrencyParameter("nisse", listOf(LockCurrencyParameter.Currency(CurrencyType.GOLD, 10)))

        `when`(lockedCurrencyService.lockCurrencies(input.characterName, input.currencies))
                .thenReturn(listOf(LockedCurrency(1, input.characterName, input.currencies[0].currencyType, input.currencies[0].amount, "FAKE-ID")))

        val result = mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/lock-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        val returnCurrencies = objectMapper.readValue<List<LockedCurrency>>(result.response.contentAsString, object : TypeReference<List<LockedCurrency>>() {})

        assertEquals(1L, returnCurrencies[0].id)
        assertEquals(input.characterName, returnCurrencies[0].characterName)
        assertEquals(input.currencies[0].currencyType, returnCurrencies[0].type)
        assertEquals(input.currencies[0].amount, returnCurrencies[0].amount)
    }

    @Test
    fun lockCurrencyWithoutCurrency() {
        val input = LockCurrencyParameter("nisse", listOf(LockCurrencyParameter.Currency(CurrencyType.GOLD, 10)))

        `when`(lockedCurrencyService.lockCurrencies(input.characterName, input.currencies))
                .thenThrow(CurrencyMissingException("Currency Missing"))

        mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/lock-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
    }

    @Test
    fun lockCurrencyWithInsufficientCurrency() {
        val input = LockCurrencyParameter("nisse", listOf(LockCurrencyParameter.Currency(CurrencyType.GOLD, 10)))

        `when`(lockedCurrencyService.lockCurrencies(input.characterName, input.currencies))
                .thenThrow(InsufficientCurrencyException("Insufficient Currency"))

        mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/lock-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
    }

    @Test
    fun abortLockedCurrencies() {
        val input = AbortLockedCurrenciesParameter("FAKE-ID")

        mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/abort-locked-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun commitLockedCurrencies() {
        val input = CommitLockedCurrenciesParameter("FAKE-ID")

        mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/commit-locked-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }
}