package com.valhallagame.valhalla.currencyserviceserver

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import com.valhallagame.valhalla.currencyserviceserver.repository.CurrencyRepository
import com.valhallagame.valhalla.currencyserviceserver.service.CurrencyService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles("test")
class CurrencyServiceTest {
    @TestConfiguration
    class CurrencyServiceTestContextConfiguration {
        @Bean
        fun currencyService(): CurrencyService {
            return CurrencyService()
        }
    }

    @Autowired
    private lateinit var currencyService: CurrencyService

    @MockBean
    private lateinit var currencyRepository: CurrencyRepository

    @Test
    fun addToExistingCurrency() {
        val currency = Currency(1, "nisse", CurrencyType.GOLD, 10)

        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(currency)
        `when`(currencyRepository.save(currency)).thenReturn(currency)

        val returnedCurrency = currencyService.addCurrency("nisse", CurrencyType.GOLD, 24)

        assertNotNull(returnedCurrency)
        assertEquals(1L, returnedCurrency.id)
        assertEquals("nisse", returnedCurrency.characterName)
        assertEquals(CurrencyType.GOLD, returnedCurrency.type)
        assertEquals(34, returnedCurrency.amount)
    }

    @Test
    fun addToMissingCurrency() {
        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(null)

        doAnswer{
            val currency = it.arguments[0] as Currency
            return@doAnswer Currency(1, currency.characterName, currency.type, currency.amount)
        }.`when`(currencyRepository).save(Mockito.any(Currency::class.java))

        val returnedCurrency = currencyService.addCurrency("nisse", CurrencyType.GOLD, 24)

        assertNotNull(returnedCurrency)
        assertEquals(1L, returnedCurrency.id)
        assertEquals("nisse", returnedCurrency.characterName)
        assertEquals(CurrencyType.GOLD, returnedCurrency.type)
        assertEquals(24, returnedCurrency.amount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun addCurrencyWithInvalidAmount() {
        currencyService.addCurrency("nisse", CurrencyType.GOLD, -32)
    }

    @Test
    fun subtractFromExistingCurrency() {
        val currency = Currency(1, "nisse", CurrencyType.GOLD, 20)

        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(currency)
        `when`(currencyRepository.save(currency)).thenReturn(currency)

        val returnedCurrency = currencyService.subtractCurrency("nisse", CurrencyType.GOLD, 7)

        assertNotNull(returnedCurrency)
        assertEquals(1L, returnedCurrency.id)
        assertEquals("nisse", returnedCurrency.characterName)
        assertEquals(CurrencyType.GOLD, returnedCurrency.type)
        assertEquals(13, returnedCurrency.amount)
    }

    @Test(expected = CurrencyMissingException::class)
    fun subtractFromMissingCurrency() {
        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(null)
        currencyService.subtractCurrency("nisse", CurrencyType.GOLD, 7)
    }

    @Test(expected = InsufficientCurrencyException::class)
    fun subtractFromInsufficientCurrency() {
        val currency = Currency(1, "nisse", CurrencyType.GOLD, 20)

        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(currency)

        currencyService.subtractCurrency("nisse", CurrencyType.GOLD, 27)
    }

    @Test
    fun getCurrency() {
        val currency = Currency(1, "nisse", CurrencyType.GOLD, 20)

        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(currency)

        val returnCurrency = currencyService.getCurrency("nisse", CurrencyType.GOLD)

        assertEquals(currency, returnCurrency)
    }

    @Test(expected = CurrencyMissingException::class)
    fun getMissingCurrency() {
        `when`(currencyRepository.findCurrencyByCharacterNameAndType("nisse", CurrencyType.GOLD)).thenReturn(null)

        currencyService.getCurrency("nisse", CurrencyType.GOLD)
    }
}