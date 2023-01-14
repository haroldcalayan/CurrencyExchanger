package com.haroldcalayan.currencyexchanger.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.haroldcalayan.currencyexchanger.data.source.local.dao.CurrencyBalanceDao
import com.haroldcalayan.currencyexchanger.data.source.local.entity.CurrencyBalance

private const val VERSION_NUMBER = 1

@Database(
    entities = [CurrencyBalance::class],
    version = VERSION_NUMBER
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun currencyBalanceDao(): CurrencyBalanceDao
}