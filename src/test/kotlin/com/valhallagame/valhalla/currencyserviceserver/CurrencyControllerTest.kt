package com.valhallagame.valhalla.currencyserviceserver

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.valhallagame.currencyserviceclient.message.AddCurrencyParameter
import com.valhallagame.currencyserviceclient.message.GetCurrenciesParameter
import com.valhallagame.currencyserviceclient.message.GetCurrencyParameter
import com.valhallagame.currencyserviceclient.message.SubtractCurrencyParameter
import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.controller.CurrencyController
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import com.valhallagame.valhalla.currencyserviceserver.service.CurrencyService
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(CurrencyController::class)
@ActiveProfiles("test")
class CurrencyControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var currencyService: CurrencyService

    private val objectMapper = ObjectMapper()

    @Test
    fun addCurrency() {
        val input = AddCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(currencyService.addCurrency(input.characterName, input.currencyType, input.amount)).thenReturn(Currency(1, input.characterName, input.currencyType, input.amount))

        val result = mvc.perform(post("/v1/currency/add-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk)
                .andReturn()

        val returnCurrency = objectMapper.readValue(result.response.contentAsString, Currency::class.java)

        assertEquals(1L, returnCurrency.id)
        assertEquals(input.characterName, returnCurrency.characterName)
        assertEquals(input.currencyType, returnCurrency.type)
        assertEquals(input.amount, returnCurrency.amount)
    }

    @Test
    fun addCurrencyWithInvalidParameter() {
        val input = AddCurrencyParameter("nisse", CurrencyType.GOLD, -10)

        `when`(currencyService.addCurrency(input.characterName, input.currencyType, input.amount)).thenReturn(Currency(1, input.characterName, input.currencyType, input.amount))

        mvc.perform(post("/v1/currency/add-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(400))
    }

    @Test
    fun subtractCurrency() {
        val input = SubtractCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(currencyService.subtractCurrency(input.characterName, input.currencyType, input.amount)).thenReturn(Currency(1, input.characterName, input.currencyType, input.amount))

        val result = mvc.perform(post("/v1/currency/subtract-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk)
                .andReturn()

        val returnCurrency = objectMapper.readValue(result.response.contentAsString, Currency::class.java)

        assertEquals(1L, returnCurrency.id)
        assertEquals(input.characterName, returnCurrency.characterName)
        assertEquals(input.currencyType, returnCurrency.type)
        assertEquals(input.amount, returnCurrency.amount)
    }

    @Test
    fun subtractCurrencyWithInvalidParameter() {
        val input = SubtractCurrencyParameter("nisse", CurrencyType.GOLD, -10)

        mvc.perform(post("/v1/currency/subtract-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(400))
    }

    @Test
    fun subtractCurrencyWithMissingCurrency() {
        val input = SubtractCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(currencyService.subtractCurrency(input.characterName, input.currencyType, input.amount)).thenThrow(CurrencyMissingException(""))

        mvc.perform(post("/v1/currency/subtract-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(404))
    }

    @Test
    fun subtractCurrencyWithInsufficientCurrency() {
        val input = SubtractCurrencyParameter("nisse", CurrencyType.GOLD, 10)

        `when`(currencyService.subtractCurrency(input.characterName, input.currencyType, input.amount)).thenThrow(InsufficientCurrencyException(""))

        mvc.perform(post("/v1/currency/subtract-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(400))
    }

    @Test
    fun getCurrency() {
        val input = GetCurrencyParameter("nisse", CurrencyType.GOLD)
        val currency = Currency(1, "nisse", CurrencyType.GOLD, 10)

        `when`(currencyService.getCurrency(input.characterName, input.currencyType)).thenReturn(currency)

        val result = mvc.perform(post("/v1/currency/get-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(200))
                .andReturn()

        assertEquals(currency, objectMapper.readValue(result.response.contentAsString, Currency::class.java))
    }

    @Test
    fun getMissingCurrency() {
        val input = GetCurrencyParameter("nisse", CurrencyType.GOLD)

        `when`(currencyService.getCurrency(input.characterName, input.currencyType)).thenThrow(CurrencyMissingException(""))

        mvc.perform(post("/v1/currency/get-currency")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(404))
    }

    @Test
    fun getCurrencies() {
        val input = GetCurrenciesParameter("nisse")
        val currency = Currency(1, "nisse", CurrencyType.GOLD, 10)

        `when`(currencyService.getCurrencies(input.characterName)).thenReturn(listOf(currency))

        val result = mvc.perform(post("/v1/currency/get-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(200))
                .andReturn()

        val currencies: List<Currency> = objectMapper.readValue(result.response.contentAsString, object : TypeReference<List<Currency>>() {} )

        assertEquals(1, currencies.size)
        assertEquals(currency, currencies[0])
    }

    @Test
    fun getCurrenciesMissingParameter() {
        val input = GetCurrenciesParameter()

        mvc.perform(post("/v1/currency/get-currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().`is`(400))
    }
}