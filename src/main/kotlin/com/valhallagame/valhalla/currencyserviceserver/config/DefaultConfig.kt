package com.valhallagame.valhalla.currencyserviceserver.config

import com.valhallagame.characterserviceclient.CharacterServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("default")
class DefaultConfig {
    @Bean
    fun characterServiceClient(): CharacterServiceClient = CharacterServiceClient.get()
}