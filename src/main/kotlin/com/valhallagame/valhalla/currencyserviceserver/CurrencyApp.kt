package com.valhallagame.valhalla.currencyserviceserver

import com.valhallagame.common.DefaultServicePortMappings
import com.valhallagame.common.filter.ServiceRequestFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import java.io.FileInputStream
import java.util.*

@SpringBootApplication
class CurrencyApp {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(CurrencyApp::class.java)
    }

    @Bean
    fun customizer() = WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        it.setPort(DefaultServicePortMappings.CURRENCY_SERVICE_PORT)
    }

    @Bean
    fun requestFilterRegistration(): FilterRegistrationBean<ServiceRequestFilter> {
        return FilterRegistrationBean<ServiceRequestFilter>().apply {
            filter = getServiceRequestFilter()
            addUrlPatterns(
                    "/*",
                    "/**"
            )
            setName("serviceRequestFilter")
            order = 1
        }
    }

    @Bean(name = ["serviceRequestFilter"])
    fun getServiceRequestFilter(): ServiceRequestFilter {
        return ServiceRequestFilter()
    }
}

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        if (CurrencyApp.logger.isInfoEnabled) {
            CurrencyApp.logger.info("Args passed in: {}", Arrays.asList(args))
        }

        args.forEach {
            val split = it.split("=")

            if (split.size == 2) {
                System.getProperties().setProperty(split[0], split[1])
            } else {
                FileInputStream(args[0]).use {
                    System.getProperties().load(it)
                }
            }
        }
    } else {
        CurrencyApp.logger.info("No args passed to main")
    }

    runApplication<CurrencyApp>(*args)
}
