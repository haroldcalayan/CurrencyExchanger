package com.haroldcalayan.currencyexchanger.common.util

import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {

    fun getCurrentDate():String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return sdf.format(Date())
    }
}