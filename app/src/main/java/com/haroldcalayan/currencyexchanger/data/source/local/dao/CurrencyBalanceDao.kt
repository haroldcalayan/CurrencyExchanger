package com.haroldcalayan.currencyexchanger.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.haroldcalayan.currencyexchanger.common.base.BaseDao
import com.haroldcalayan.currencyexchanger.data.source.local.entity.CurrencyBalance

@Dao
interface CurrencyBalanceDao: BaseDao<CurrencyBalance> {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun all(): List<CurrencyBalance>?

    @Query("SELECT * FROM $TABLE_NAME WHERE currencyName = :currencyName")
    suspend fun get(currencyName: Int): CurrencyBalance?

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()

    companion object {
        const val TABLE_NAME = "currencyBalance"
    }
}