package com.valhallagame.valhalla.currencyserviceserver.job

import com.valhallagame.valhalla.currencyserviceserver.service.LockedCurrencyService
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class StaleLockedCurrencyJob {
    @Autowired
    private lateinit var lockedCurrencyService: LockedCurrencyService

    @Value("\${spring.application.name}")
    private val appName: String? = null

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 1000 * 60)
    fun execute() {
        MDC.put("service_name", appName)
        MDC.put("request_id", UUID.randomUUID().toString())

        try {
            lockedCurrencyService.abortStaleLockedCurrencies()
        } finally {
            MDC.clear()
        }
    }
}