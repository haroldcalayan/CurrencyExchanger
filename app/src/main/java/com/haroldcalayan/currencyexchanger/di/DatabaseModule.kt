package com.haroldcalayan.currencyexchanger.di

import android.content.Context
import androidx.room.Room
import com.haroldcalayan.currencyexchanger.BuildConfig
import com.haroldcalayan.currencyexchanger.data.source.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            BuildConfig.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideCurrencyExchangeDao(database: AppDatabase) = database.currencyBalanceDao()
}