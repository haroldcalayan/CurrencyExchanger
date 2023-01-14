package com.haroldcalayan.currencyexchanger.data.source.remote.service

import com.haroldcalayan.currencyexchanger.data.source.remote.dto.ExchangeRatesDTO
import retrofit2.http.GET

interface CurrencyApi {
    @GET("currency-exchange-rates")
    suspend fun getExchangeRate() : ExchangeRatesDTO
}