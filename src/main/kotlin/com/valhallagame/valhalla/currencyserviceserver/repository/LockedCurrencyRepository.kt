package com.valhallagame.valhalla.currencyserviceserver.repository

import com.valhallagame.valhalla.currencyserviceserver.model.LockedCurrency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import javax.transaction.Transactional

interface LockedCurrencyRepository : JpaRepository<LockedCurrency, Long> {
    fun findLockedCurrencyByLockingId(lockingId: String): List<LockedCurrency>
    fun deleteLockedCurrencyByLockingId(lockingId: String)

    @Query(value = "SELECT DISTINCT locking_id FROM locked_currency WHERE created < now() - INTERVAL '1 MINUTE'", nativeQuery = true)
    fun findOldLockedCurrencyLockingIds(): List<String>

    @Modifying
    @Transactional
    fun deleteLockedCurrencyByCharacterName(characterName: String)
}