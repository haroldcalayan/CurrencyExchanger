package com.haroldcalayan.currencyexchanger.domain.use_case

import com.haroldcalayan.currencyexchanger.common.Response
import com.haroldcalayan.currencyexchanger.data.repository.CurrencyRepository
import com.haroldcalayan.currencyexchanger.data.source.local.entity.CurrencyBalance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class SetCurrencyBalanceUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    operator fun invoke(currencyBalance: MutableList<CurrencyBalance>): Flow<Response<Unit>> = flow {
        try {
            val data = repository.setCurrencyBalance(currencyBalance)
            emit(Response.Success(data))
        } catch (e: IOException) {
            emit(Response.Error("insertion Failed"))
        }
    }
}