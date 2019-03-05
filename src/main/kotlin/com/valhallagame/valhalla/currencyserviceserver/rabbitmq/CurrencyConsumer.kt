package com.valhallagame.valhalla.currencyserviceserver.rabbitmq

import com.valhallagame.common.rabbitmq.NotificationMessage
import com.valhallagame.valhalla.currencyserviceserver.service.CurrencyService
import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.lang.Exception
import java.util.*

@Component
class CurrencyConsumer
    @Autowired
    constructor(
            private val currencyService: CurrencyService,
            private val lockedCurrencyService: LockedCurrencyService
    ){

    companion object {
        private val logger = LoggerFactory.getLogger(CurrencyConsumer::class.java)
    }

    @Value("\${spring.application.name}")
    private val appName: String? = null

    @RabbitListener(queues = ["#{currencyCharacterDeleteQueue.name}"])
    fun receivedCharacterDeleteNotification(notificationMessage: NotificationMessage) {
        MDC.put("service_name", appName)
        MDC.put("request_id", notificationMessage.data["requestId"] as String? ?: UUID.randomUUID().toString())

        logger.info("Received Character Delete Notification with message {}", notificationMessage)

        try {
            val characterName = notificationMessage.data["characterName"] as String
            currencyService.deleteCurrencyByCharacterName(characterName)
            lockedCurrencyService.deleteLockedCurrencyByCharacterName(characterName)
        } catch(e: Exception) {
            logger.error("Error while processing Character Delete notification", e)
        } finally {
            MDC.clear()
        }
    }
}