package com.valhallagame.valhalla.currencyserviceserver.service

import com.valhallagame.characterserviceclient.CharacterServiceClient
import com.valhallagame.common.rabbitmq.NotificationMessage
import com.valhallagame.common.rabbitmq.RabbitMQRouting
import com.valhallagame.common.rabbitmq.RabbitSender
import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.featserviceclient.message.FeatName
import com.valhallagame.valhalla.currencyserviceserver.exception.CurrencyMissingException
import com.valhallagame.valhalla.currencyserviceserver.exception.InsufficientCurrencyException
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import com.valhallagame.valhalla.currencyserviceserver.repository.CurrencyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CurrencyService {
    companion object {
        private val logger = LoggerFactory.getLogger(CurrencyService::class.java)
    }

    @Autowired
    private lateinit var currencyRepository: CurrencyRepository

    @Autowired
    private lateinit var characterServiceClient: CharacterServiceClient

    @Autowired
    private lateinit var rabbitSender: RabbitSender

    fun addCurrency(characterName: String, currencyType: CurrencyType, amount: Int, notify: Boolean = false): Currency {
        logger.info("Adding {} {} currency to {}", amount, currencyType, characterName)
        if(amount < 0) {
            logger.error("Tried to add a negative amount {}", amount)
            throw IllegalArgumentException("Amount my not be lesser than 0")
        }

        val currentCurrency = currencyRepository.findCurrencyByCharacterNameAndType(characterName, currencyType) ?: Currency(characterName = characterName, type = currencyType, amount = 0)
        currentCurrency.amount += amount

        val savedCurrency = currencyRepository.save(currentCurrency)

        if(notify) {
            val response = characterServiceClient.getCharacter(characterName)

            if(response.isOk) {
                val message = NotificationMessage(response.get().get().ownerUsername, "Currency Added").apply {
                    addData("currency", currencyType)
                    addData("amount", amount)
                }
                rabbitSender.sendMessage(RabbitMQRouting.Exchange.CURRENCY, RabbitMQRouting.Currency.ADD.name, message)
            }

        }

        return savedCurrency
    }

    // Annotation not needed in Kotlin. Is only there to make mockito happy
    @Throws(CurrencyMissingException::class, InsufficientCurrencyException::class)
    fun subtractCurrency(characterName: String, currencyType: CurrencyType, amount: Int): Currency {
        logger.info("Subtracting {} {} currency to {}", amount, currencyType, characterName)
        if(amount < 0) {
            logger.error("Tried to subtract a negative amount {}", amount)
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
    fun getCurrency(characterName: String, currencyType: CurrencyType): Currency {
        logger.info("Getting currency {} for {}", currencyType, characterName)
        return currencyRepository.findCurrencyByCharacterNameAndType(characterName, currencyType) ?: throw CurrencyMissingException("Unable to find currency ${currencyType.name} for $characterName")
    }

    fun getCurrencies(characterName: String): List<Currency> {
        logger.info("Getting all currencies for {}", characterName)
        return currencyRepository.findCurrencyByCharacterName(characterName)
    }

    fun deleteCurrencyByCharacterName(characterName: String) {
        logger.info("Deleting all currencies for {}", characterName)
        currencyRepository.deleteCurrencyByCharacterName(characterName)
    }

    fun addCurrencyFromFeat(characterName: String, featName: FeatName) {
        logger.info("Adding currency from feat {} for {}", featName, characterName)
        when (featName) {
            FeatName.MISSVEDEN_SAXUMPHILE -> {
                addCurrency(characterName, CurrencyType.GOLD, 50, true)
            }
            FeatName.MISSVEDEN_NO_LESSER_FOES -> {
                addCurrency(characterName, CurrencyType.GOLD, 50, true)
            }
            FeatName.FREDSTORP_ANORECTIC -> {
                addCurrency(characterName, CurrencyType.GOLD, 50, true)
            }
            FeatName.FREDSTORP_EXTERMINATOR -> {
                addCurrency(characterName, CurrencyType.IRON, 100, true)
                addCurrency(characterName, CurrencyType.WOOD, 100, true)
                addCurrency(characterName, CurrencyType.GOLD, 200, true)
                addCurrency(characterName, CurrencyType.CONCENTRATED_HATRED, 2, true)
            }
            FeatName.HJUO_LIKE_THE_WIND -> {
                addCurrency(characterName, CurrencyType.GOLD, 50, true)
            }
            FeatName.HJUO_PRECAUTIONS -> {
                addCurrency(characterName, CurrencyType.GOLD, 50, true)
            }
            FeatName.HJUO_THERMAL_VELOCITY -> {
                addCurrency(characterName, CurrencyType.GOLD, 50, true)
            }
            else -> {}
        }
    }
}