package com.haroldcalayan.currencyexchanger.domain.use_case

import com.haroldcalayan.currencyexchanger.common.Response
import com.haroldcalayan.currencyexchanger.data.repository.CurrencyRepository
import com.haroldcalayan.currencyexchanger.data.source.remote.dto.ExchangeRatesDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetExchangeRatesUseCase@Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<Response<ExchangeRatesDTO>> = flow {
        try {
            emit(Response.Loading())
            val data = repository.getExchangeRates()
            emit(Response.Success(data))
        } catch (e: HttpException) {
            emit(Response.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Response.Error("Couldn't reach server. Check your internet connection"))
        }
    }
}