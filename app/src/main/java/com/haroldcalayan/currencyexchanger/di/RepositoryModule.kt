package com.haroldcalayan.currencyexchanger.di

import com.haroldcalayan.currencyexchanger.data.repository.CurrencyRepository
import com.haroldcalayan.currencyexchanger.data.repository.CurrencyRepositoryImpl
import com.haroldcalayan.currencyexchanger.data.source.local.database.AppDatabase
import com.haroldcalayan.currencyexchanger.data.source.remote.service.CurrencyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCurrencyRepository(api: CurrencyApi, db: AppDatabase): CurrencyRepository {
        return CurrencyRepositoryImpl(api, db)
    }
}