package com.valhallagame.valhalla.currencyserviceserver.repository

import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import org.springframework.data.jpa.repository.JpaRepository

interface LockedCurrencyRepository : JpaRepository<LockedCurrency, Long> {
    fun findLockedCurrencyByLockingId(lockingId: String): List<LockedCurrency>
    fun deleteLockedCurrencyByLockingId(lockingId: String)
}