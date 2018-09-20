package com.valhallagame.valhalla.currencyserviceserver

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import com.valhallagame.valhalla.currencyserviceserver.repository.LockedCurrencyRepository
import com.valhallagame.valhalla.currencyserviceserver.service.CurrencyService
import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles("test")
class LockedCurrencyServiceTest {
    @TestConfiguration
    class LockedCurrencyServiceTestContextConfiguration {
        @Bean
        fun lockedCurrencyService(): LockedCurrencyService {
            return LockedCurrencyService()
        }
    }

    @Autowired
    private lateinit var lockedCurrencyService: LockedCurrencyService

    @MockBean
    private lateinit var lockedCurrencyRepository: LockedCurrencyRepository

    @MockBean
    private lateinit var currencyService: CurrencyService

    @Test
    fun lockCurrency() {
        `when`(currencyService.getCurrency("nisse", CurrencyType.GOLD))
                .thenReturn(Currency(1, "nisse", CurrencyType.GOLD, 100))

        doAnswer {
            val lockedCurrency = it.arguments[0] as LockedCurrency
            return@doAnswer LockedCurrency(1, lockedCurrency.characterName, lockedCurrency.type, lockedCurrency.amount)
        }.`when`(lockedCurrencyRepository).save(any(LockedCurrency::class.java))

        val lockedCurrency = lockedCurrencyService.lockCurrency("nisse", 50, CurrencyType.GOLD)

        assertEquals(lockedCurrency.id, 1L)
        assertEquals(lockedCurrency.amount, 50)
        assertEquals(lockedCurrency.type, CurrencyType.GOLD)
        assertEquals(lockedCurrency.characterName, "nisse")
    }

    @Test(expected = CurrencyMissingException::class)
    fun lockCurrencyWithoutCurrency() {
        `when`(currencyService.getCurrency("nisse", CurrencyType.GOLD))
                .thenThrow(CurrencyMissingException("Currency Missing"))
        lockedCurrencyService.lockCurrency("nisse", 50, CurrencyType.GOLD)
    }

    @Test(expected = InsufficientCurrencyException::class)
    fun lockCurrencyWithInsufficientCurrency() {
        `when`(currencyService.getCurrency("nisse", CurrencyType.GOLD))
                .thenReturn(Currency(1, "nisse", CurrencyType.GOLD, 10))

        doAnswer {
            val lockedCurrency = it.arguments[0] as LockedCurrency
            return@doAnswer LockedCurrency(1, lockedCurrency.characterName, lockedCurrency.type, lockedCurrency.amount)
        }.`when`(lockedCurrencyRepository).save(any(LockedCurrency::class.java))

        lockedCurrencyService.lockCurrency("nisse", 50, CurrencyType.GOLD)
    }
}