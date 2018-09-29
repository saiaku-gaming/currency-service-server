package com.valhallagame.valhalla.currencyserviceserver

import com.valhallagame.currencyserviceclient.message.LockCurrencyParameter
import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
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
    fun lockCurrencies() {
        doAnswer {
            val lockedCurrency = it.arguments[0] as LockedCurrency
            return@doAnswer LockedCurrency(1, lockedCurrency.characterName, lockedCurrency.type, lockedCurrency.amount, "FAKE-ID")
        }.`when`(lockedCurrencyRepository).save(any(LockedCurrency::class.java))

        val lockedCurrency = lockedCurrencyService.lockCurrencies("nisse", listOf(LockCurrencyParameter.Currency(CurrencyType.GOLD, 50)))

        assertEquals(lockedCurrency[0].id, 1L)
        assertEquals(lockedCurrency[0].amount, 50)
        assertEquals(lockedCurrency[0].type, CurrencyType.GOLD)
        assertEquals(lockedCurrency[0].characterName, "nisse")
    }

    @Test(expected = CurrencyMissingException::class)
    fun lockCurrencyWithoutCurrencies() {
        `when`(currencyService.subtractCurrency("nisse", CurrencyType.GOLD, 50))
                .thenThrow(CurrencyMissingException("Currency Missing"))
        lockedCurrencyService.lockCurrencies("nisse", listOf(LockCurrencyParameter.Currency(CurrencyType.GOLD, 50)))
    }

    @Test(expected = InsufficientCurrencyException::class)
    fun lockCurrencyWithInsufficientCurrencies() {
        `when`(currencyService.subtractCurrency("nisse", CurrencyType.GOLD, 50))
                .thenThrow(InsufficientCurrencyException("Not enough currencies"))

        lockedCurrencyService.lockCurrencies("nisse", listOf(LockCurrencyParameter.Currency(CurrencyType.GOLD, 50)))
    }

    @Test
    fun abortLockedCurrencies() {
        `when`(lockedCurrencyRepository.findLockedCurrencyByLockingId("FAKE-ID"))
                .thenReturn(listOf(LockedCurrency(1, "nisse", CurrencyType.GOLD, 10, "FAKE-ID")))

        lockedCurrencyService.abortLockedCurrencies("FAKE-ID")
    }

    @Test
    fun commitLockedCurrencies() {
        lockedCurrencyService.commitLockedCurrencies("FAKE-ID")
    }
}