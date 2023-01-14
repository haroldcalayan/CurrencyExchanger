package com.haroldcalayan.currencyexchanger.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.haroldcalayan.currencyexchanger.common.util.toRoundedDecimalString

@Entity(tableName = "currencyBalance", indices = [Index(value = ["currencyName"], unique = true)])
data class CurrencyBalance(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val currencyName: String? = null,
    val balance: Double? = 0.00,
    val cashInDate: String?= null,
    val isBase: Boolean? = false
) {

    fun getRoundedDecimalFormat(balance: Double): String {
        return balance.toString().toRoundedDecimalString()
    }
}