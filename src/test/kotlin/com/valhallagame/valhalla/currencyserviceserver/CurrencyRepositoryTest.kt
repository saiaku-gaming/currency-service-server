package com.valhallagame.valhalla.currencyserviceserver

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import com.valhallagame.valhalla.currencyserviceserver.repository.CurrencyRepository
import org.junit.Assert.*
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
class CurrencyRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var currencyRepository: CurrencyRepository

    @Test
    fun save() {
        val currency = Currency(characterName = "nisse", type = CurrencyType.GOLD, amount = 10)

        val savedCurrency = currencyRepository.save(currency)

        assertNotNull(savedCurrency.id)
        assertEquals(currency.amount, savedCurrency.amount)
        assertEquals(currency.characterName, savedCurrency.characterName)
        assertEquals(currency.type, savedCurrency.type)
    }

    @Test
    fun delete() {
        val currency = Currency(characterName = "nisse", type = CurrencyType.GOLD, amount = 10)

        entityManager.persist(currency)
        entityManager.flush()

        val foundCurrency = entityManager.find(Currency::class.java, currency.id)

        assertNotNull(foundCurrency)

        currencyRepository.delete(foundCurrency)

        assertNull(entityManager.find(Currency::class.java, currency.id))
    }

    @Test
    fun findCurrencyByCharacterName() {
        val currency = Currency(characterName = "nisse", type = CurrencyType.GOLD, amount = 10)

        entityManager.persist(currency)
        entityManager.flush()

        val foundCurrency = currencyRepository.findCurrencyByCharacterNameAndType(currency.characterName, CurrencyType.GOLD)

        assertNotNull(foundCurrency)
        assertEquals(currency.characterName, foundCurrency?.characterName)
        assertEquals(currency.type, foundCurrency?.type)
        assertEquals(currency.amount, foundCurrency?.amount)
    }

    @Test
    fun findMissingCurrencyByCharacterName() {
        val currency = Currency(characterName = "nisse", type = CurrencyType.GOLD, amount = 10)

        entityManager.persist(currency)
        entityManager.flush()

        val foundCurrency = currencyRepository.findCurrencyByCharacterNameAndType("hult", CurrencyType.GOLD)

        assertNull(foundCurrency)
    }
}