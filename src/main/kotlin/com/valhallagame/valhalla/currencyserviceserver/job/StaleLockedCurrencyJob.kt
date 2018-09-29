package com.valhallagame.valhalla.currencyserviceserver.job

import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class StaleLockedCurrencyJob {
    @Autowired
    private lateinit var lockedCurrencyService: LockedCurrencyService

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1000 * 60)
    fun execute() {
        lockedCurrencyService.abortStaleLockedCurrencies()
    }
}