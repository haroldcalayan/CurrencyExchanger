package com.haroldcalayan.currencyexchanger.common.util

fun String.toRoundedDecimalString(): String {
    val number = (this.toBigDecimal()).toPlainString()
    val decimal = number.split(".")[1]
    return if (!decimal.startsWith("00")) {
        String.format("%.2f", number.toBigDecimal())
    } else {
        when(decimal.length) {
            in 0..2 -> String.format("%.2f", number.toBigDecimal())
            3 -> String.format("%.3f", number.toBigDecimal())
            4 -> String.format("%.4f", number.toBigDecimal())
            5 -> String.format("%.5f", number.toBigDecimal())
            6 -> String.format("%.6f", number.toBigDecimal())
            else -> String.format("%.7f", number.toBigDecimal())
        }
    }
}

fun Double?.toRoundedDouble(): Double {
    val number = ((this ?: 0.00).toBigDecimal()).toPlainString()
    val decimal = number.split(".")[1]
    return if (!decimal.startsWith("00")) {
        String.format("%.2f", number.toBigDecimal())
    } else {
        when(decimal.length) {
            in 0..2 -> String.format("%.2f", number.toBigDecimal())
            3 -> String.format("%.3f", number.toBigDecimal())
            4 -> String.format("%.4f", number.toBigDecimal())
            5 -> String.format("%.5f", number.toBigDecimal())
            6 -> String.format("%.6f", number.toBigDecimal())
            else -> String.format("%.7f", number.toBigDecimal())
        }
    }.toDouble()
}