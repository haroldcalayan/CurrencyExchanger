package com.haroldcalayan.currencyexchanger.data.source.remote.dto


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRatesDTO(
    val base: String? = null,
    val date: String? = null,
    val rates: Map<String, Double>? = null
)