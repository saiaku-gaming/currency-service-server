package com.valhallagame.valhalla.currencyserviceserver

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import com.valhallagame.valhalla.currencyserviceserver.repository.LockedCurrencyRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@ActiveProfiles("test")
class LockedCurrencyRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var lockedCurrencyRepository: LockedCurrencyRepository

    @Test
    fun findLockedCurrencyByLockingId() {
        entityManager.persist(LockedCurrency(characterName = "nisse", type = CurrencyType.GOLD, amount = 10, lockingId = "FAKE-ID"))
        entityManager.persist(LockedCurrency(characterName = "nisse", type = CurrencyType.GOLD, amount = 50, lockingId = "FAKE-ID-2"))

        val lockedCurrencies = lockedCurrencyRepository.findLockedCurrencyByLockingId("FAKE-ID")

        assertEquals(1, lockedCurrencies.size)
        assertEquals("nisse", lockedCurrencies[0].characterName)
        assertEquals(10, lockedCurrencies[0].amount)
        assertEquals(CurrencyType.GOLD, lockedCurrencies[0].type)
        assertEquals("FAKE-ID", lockedCurrencies[0].lockingId)
    }

    @Test
    fun deleteLockedCurrencyByLockingId() {
        entityManager.persist(LockedCurrency(characterName = "nisse", type = CurrencyType.GOLD, amount = 10, lockingId = "FAKE-ID"))
        entityManager.persist(LockedCurrency(characterName = "nisse", type = CurrencyType.GOLD, amount = 50, lockingId = "FAKE-ID-2"))

        lockedCurrencyRepository.deleteLockedCurrencyByLockingId("FAKE-ID")

        val lockedCurrencies = lockedCurrencyRepository.findAll()
        assertEquals(1, lockedCurrencies.size)
        assertEquals("nisse", lockedCurrencies[0].characterName)
        assertEquals(50, lockedCurrencies[0].amount)
        assertEquals(CurrencyType.GOLD, lockedCurrencies[0].type)
        assertEquals("FAKE-ID-2", lockedCurrencies[0].lockingId)
    }
}