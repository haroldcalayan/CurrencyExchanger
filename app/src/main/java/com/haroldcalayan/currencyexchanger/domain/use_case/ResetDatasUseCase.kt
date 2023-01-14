package com.haroldcalayan.currencyexchanger.domain.use_case

import com.haroldcalayan.currencyexchanger.common.Response
import com.haroldcalayan.currencyexchanger.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ResetDatasUseCase@Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(): Flow<Response<Unit>> = flow {
        try {
            val data = repository.resetData()
            emit(Response.Success(data))
        } catch (e: IOException) {
            emit(Response.Error("reset Failed"))
        }
    }
}