package com.haroldcalayan.currencyexchanger.common

object Constants {
    private const val ONE_SECOND = 1000L
    const val SPLASH_SCREEN_LIFE = 2 * ONE_SECOND
    const val PERCENTAGE = 100

    const val BASE_CURRENCY = "EUR"
    const val DEFAULT_CURRENCY_VALUE = "0.00"

    const val NO_CURRENCY_VALUE = 0.00
    const val INITIAL_BALANCE_VALUE = 1000.00

    //Implement Rules
    const val FREE_TRIAL = 5
    const val MINIMUM_ENTRY = 0.00
    const val COMMISSION = 0.7

    const val CURRENT_COMMISSION = COMMISSION / PERCENTAGE
}