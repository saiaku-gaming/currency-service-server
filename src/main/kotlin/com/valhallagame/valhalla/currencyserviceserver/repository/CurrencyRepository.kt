package com.valhallagame.valhalla.currencyserviceserver.repository

import com.valhallagame.currencyserviceclient.model.CurrencyType
import com.valhallagame.valhalla.currencyserviceserver.model.Currency
import org.springframework.data.jpa.repository.JpaRepository

interface CurrencyRepository : JpaRepository<Currency, Long> {
    fun findCurrencyByCharacterNameAndType(characterName: String, type: CurrencyType): Currency?
}