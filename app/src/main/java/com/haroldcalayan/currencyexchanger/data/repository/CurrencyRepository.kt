package com.haroldcalayan.currencyexchanger.data.repository

import com.haroldcalayan.currencyexchanger.data.source.local.database.AppDatabase
import com.haroldcalayan.currencyexchanger.data.source.local.entity.CurrencyBalance
import com.haroldcalayan.currencyexchanger.data.source.remote.dto.ExchangeRatesDTO
import com.haroldcalayan.currencyexchanger.data.source.remote.service.CurrencyApi

interface CurrencyRepository {
    suspend fun getExchangeRates(): ExchangeRatesDTO
    suspend fun getCurrencyBalance(): List<CurrencyBalance>
    suspend fun setCurrencyBalance(balance: List<CurrencyBalance>)
    suspend fun resetData()
}

class CurrencyRepositoryImpl(private val api: CurrencyApi,
                             private val db: AppDatabase
): CurrencyRepository {
    override suspend fun getExchangeRates(): ExchangeRatesDTO {
        return api.getExchangeRate()
    }

    override suspend fun getCurrencyBalance(): List<CurrencyBalance> {
        return db.currencyBalanceDao().all() ?: emptyList()
    }

    override suspend fun setCurrencyBalance(balance: List<CurrencyBalance>) {
        return db.currencyBalanceDao().insertAll(balance)
    }

    override suspend fun resetData() {
        return db.currencyBalanceDao().nukeTable()
    }
}