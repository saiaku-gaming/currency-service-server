package com.valhallagame.valhalla.currencyserviceserver.model

import com.valhallagame.currencyserviceclient.model.CurrencyType
import java.time.Instant
import javax.persistence.*

@Entity
data class LockedCurrency(
        @Id
        @SequenceGenerator(name = "locked_currency_locked_currency_id_seq", sequenceName = "locked_currency_locked_currency_id_seq", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locked_currency_locked_currency_id_seq")
        @Column(name = "locked_currency_id")
        val id: Long? = null,

        @Column(name = "character_name")
        val characterName: String,

        @Enumerated(EnumType.STRING)
        @Column(name = "type")
        val type: CurrencyType,

        @Column(name = "amount")
        var amount: Int,

        @Column(name = "locking_id")
        val lockingId: String,

        @Column(name = "created")
        val created: Instant = Instant.now()
)