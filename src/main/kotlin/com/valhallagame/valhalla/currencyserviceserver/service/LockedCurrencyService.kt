package com.valhallagame.valhalla.currencyserviceserver.service

import com.valhallagame.currencyserviceclient.message.LockCurrencyParameter
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import com.valhallagame.valhalla.currencyserviceserver.repository.LockedCurrencyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class LockedCurrencyService {
    companion object {
        private val logger = LoggerFactory.getLogger(LockedCurrencyService::class.java)
    }

    @Autowired
    private lateinit var lockedCurrencyRepository: LockedCurrencyRepository

    @Autowired
    private lateinit var currencyService: CurrencyService

    @Throws(CurrencyMissingException::class, InsufficientCurrencyException::class)
    fun lockCurrencies(characterName: String, currencies: List<LockCurrencyParameter.Currency>): List<LockedCurrency> {
        logger.info("Locking currencies {} for {}", currencies, characterName)
        val lockedCurrencies = mutableListOf<LockedCurrency>()
        val lockingId = UUID.randomUUID().toString()

        currencies.forEach {
            try {
                currencyService.subtractCurrency(characterName, it.currencyType, it.amount)
            } catch (e: Exception) {
                abortLockedCurrencies(lockingId)
                throw e
            }

            lockedCurrencies.add(lockedCurrencyRepository.save(LockedCurrency(characterName = characterName,
                    amount = it.amount, type = it.currencyType, lockingId = lockingId)))
        }

        return lockedCurrencies
    }

    fun abortLockedCurrencies(lockingId: String) {
        logger.info("Aborting locked currecies with id {}", lockingId)
        val lockedCurrencies = lockedCurrencyRepository.findLockedCurrencyByLockingId(lockingId)

        lockedCurrencies.forEach {
            currencyService.addCurrency(it.characterName, it.type, it.amount)
            lockedCurrencyRepository.delete(it)
        }
    }

    fun commitLockedCurrencies(lockedId: String) {
        logger.info("Commiting locked currencies with id {}", lockedId)
        lockedCurrencyRepository.deleteLockedCurrencyByLockingId(lockedId)
    }

    fun abortStaleLockedCurrencies() {
        logger.info("Aborting stale locked currencies")
        val lockingIds = lockedCurrencyRepository.findOldLockedCurrencyLockingIds()

        lockingIds.forEach {
            abortLockedCurrencies(it)
        }
    }

    fun deleteLockedCurrencyByCharacterName(characterName: String) {
        logger.info("Deleting all locked currencies for {}", characterName)
        lockedCurrencyRepository.deleteLockedCurrencyByCharacterName(characterName)
    }
}