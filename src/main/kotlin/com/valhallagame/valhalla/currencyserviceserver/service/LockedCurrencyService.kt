package com.valhallagame.valhalla.currencyserviceserver.service

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import com.valhallagame.valhalla.currencyserviceserver.repository.LockedCurrencyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LockedCurrencyService {
    @Autowired
    private lateinit var lockedCurrencyRepository: LockedCurrencyRepository

    @Autowired
    private lateinit var currencyService: CurrencyService

    @Throws(CurrencyMissingException::class, InsufficientCurrencyException::class)
    fun lockCurrency(characterName: String, amount: Int, currencyType: CurrencyType): LockedCurrency {
        val currency = currencyService.getCurrency(characterName, currencyType)

        if(currency.amount < amount) {
            throw InsufficientCurrencyException("Could not lock $amount of ${currency.amount} $currencyType")
        }

        return lockedCurrencyRepository.save(LockedCurrency(characterName = characterName, amount = amount, type = currencyType))
    }
}