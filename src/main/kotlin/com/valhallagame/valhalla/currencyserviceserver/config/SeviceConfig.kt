package com.valhallagame.valhalla.currencyserviceserver.config

import com.valhallagame.characterserviceclient.CharacterServiceClient
import com.valhallagame.common.DefaultServicePortMappings
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("production", "development")
class SeviceConfig {
    @Bean
    fun characterServiceClient(): CharacterServiceClient {
        CharacterServiceClient.init("http://character-service:" + DefaultServicePortMappings.CHARACTER_SERVICE_PORT)
        return CharacterServiceClient.get()
    }
}