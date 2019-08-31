package com.valhallagame.valhalla.currencyserviceserver.config

import com.valhallagame.common.rabbitmq.RabbitMQRouting
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun characterExchange() = DirectExchange(RabbitMQRouting.Exchange.CHARACTER.name)

    @Bean
    fun featExchange(): DirectExchange {
        return DirectExchange(RabbitMQRouting.Exchange.FEAT.name)
    }

    @Bean
    fun currencyFeatAddQueue(): Queue {
        return Queue("currencyFeatAddQueue")
    }

    @Bean
    fun bindingFeatAdd(featExchange: DirectExchange, currencyFeatAddQueue: Queue): Binding {
        return BindingBuilder.bind(currencyFeatAddQueue).to(featExchange).with(RabbitMQRouting.Feat.ADD)
    }

    @Bean
    fun currencyCharacterDeleteQueue() = Queue("currencyCharacterDeleteQueue")

    @Bean
    fun bindingCharacterDelete(characterExchange: DirectExchange, currencyCharacterDeleteQueue: Queue): Binding
            = BindingBuilder.bind(currencyCharacterDeleteQueue).to(characterExchange).with(RabbitMQRouting.Character.DELETE)

    @Bean
    fun jacksonConverter() = Jackson2JsonMessageConverter()

    @Bean
    fun containerFactory(): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setMessageConverter(jacksonConverter())
        return factory
    }
}