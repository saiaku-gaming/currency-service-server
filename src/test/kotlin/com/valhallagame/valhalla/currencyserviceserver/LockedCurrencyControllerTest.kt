package com.valhallagame.valhalla.currencyserviceserver

import com.fasterxml.jackson.databind.ObjectMapper
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

    @Test
    fun lockCurrency() {
        val input = LockCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(lockedCurrencyService.lockCurrency(input.characterName, input.amount, input.currencyType))
                .thenReturn(LockedCurrency(1, input.characterName, input.currencyType, input.amount))

        val result = mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/lock-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        val returnCurrency = objectMapper.readValue(result.response.contentAsString, LockedCurrency::class.java)

        assertEquals(1L, returnCurrency.id)
        assertEquals(input.characterName, returnCurrency.characterName)
        assertEquals(input.currencyType, returnCurrency.type)
        assertEquals(input.amount, returnCurrency.amount)
    }

    @Test
    fun lockCurrencyWithoutCurrency() {
        val input = LockCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(lockedCurrencyService.lockCurrency(input.characterName, input.amount, input.currencyType))
                .thenThrow(CurrencyMissingException("Currency Missing"))

        mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/lock-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
    }

    @Test
    fun lockCurrencyWithInsufficientCurrency() {
        val input = LockCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(lockedCurrencyService.lockCurrency(input.characterName, input.amount, input.currencyType))
                .thenThrow(InsufficientCurrencyException("Insufficient Currency"))

        mvc.perform(MockMvcRequestBuilders.post("/v1/locked-currency/lock-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(MockMvcResultMatchers.status().`is`(400))
    }
}