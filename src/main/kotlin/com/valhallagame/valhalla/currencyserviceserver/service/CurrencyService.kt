package com.valhallagame.valhalla.currencyserviceserver.service

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import com.valhallagame.valhalla.currencyserviceserver.repository.CurrencyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CurrencyService {
    @Autowired
    private lateinit var currencyRepository: CurrencyRepository

    fun addCurrency(characterName: String, currencyType: CurrencyType, amount: Int): Currency {
        if(amount < 0) {
            throw IllegalArgumentException("Amount my not be lesser than 0")
        }

        val currentCurrency = currencyRepository.findCurrencyByCharacterNameAndType(characterName, currencyType) ?: Currency(characterName = characterName, type = currencyType, amount = 0)
        currentCurrency.amount += amount
        return currencyRepository.save(currentCurrency)
    }

    // Annotation not needed in Kotlin. Is only there to make mockito happy
    @Throws(CurrencyMissingException::class, InsufficientCurrencyException::class)
    fun subtractCurrency(characterName: String, currencyType: CurrencyType, amount: Int): Currency {
        if(amount < 0) {
            throw IllegalArgumentException("Amount my not be lesser than 0")
        }

        val currentCurrency = currencyRepository.findCurrencyByCharacterNameAndType(characterName, currencyType) ?: throw CurrencyMissingException("Unable to find currency ${currencyType.name} for $characterName")

        if(currentCurrency.amount < amount) {
            throw InsufficientCurrencyException("Not enough ${currencyType.name}. Tried to subtract $amount but only had ${currentCurrency.amount}")
        }

        currentCurrency.amount -= amount
        return currencyRepository.save(currentCurrency)
    }

    // Annotation not needed in Kotlin. Is only there to make mockito happy
    @Throws(CurrencyMissingException::class)
    fun getCurrency(characterName: String, currencyType: CurrencyType)
            = currencyRepository.findCurrencyByCharacterNameAndType(characterName, currencyType) ?: throw CurrencyMissingException("Unable to find currency ${currencyType.name} for $characterName")

    fun getCurrencies(characterName: String) = currencyRepository.findCurrencyByCharacterName(characterName)

    fun deleteCurrencyByCharacterName(characterName: String) {
        currencyRepository.deleteCurrencyByCharacterName(characterName)
    }
}