package com.valhallagame.valhalla.currencyserviceserver.rabbitmq

import com.valhallagame.common.rabbitmq.NotificationMessage
import com.valhallagame.valhalla.currencyserviceserver.service.CurrencyService
import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CurrencyConsumer
    @Autowired
    constructor(
            private val currencyService: CurrencyService,
            private val lockedCurrencyService: LockedCurrencyService
    ){

    @RabbitListener(queues = ["#{currencyCharacterDeleteQueue.name}"])
    fun receivedCharacterDeleteNotification(notificationMessage: NotificationMessage) {
        val characterName = notificationMessage.data["characterName"] as String
        currencyService.deleteCurrencyByCharacterName(characterName)
        lockedCurrencyService.deleteLockedCurrencyByCharacterName(characterName)
    }
}