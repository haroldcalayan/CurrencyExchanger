package com.haroldcalayan.currencyexchanger.presentation.ui.currency_exchange

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.haroldcalayan.currencyexchanger.common.Constants.BASE_CURRENCY
import com.haroldcalayan.currencyexchanger.common.Constants.CURRENT_COMMISSION
import com.haroldcalayan.currencyexchanger.common.Constants.DEFAULT_CURRENCY_VALUE
import com.haroldcalayan.currencyexchanger.common.Constants.FREE_TRIAL
import com.haroldcalayan.currencyexchanger.common.Constants.INITIAL_BALANCE_VALUE
import com.haroldcalayan.currencyexchanger.common.Constants.MINIMUM_ENTRY
import com.haroldcalayan.currencyexchanger.common.Constants.NO_CURRENCY_VALUE
import com.haroldcalayan.currencyexchanger.common.DataState
import com.haroldcalayan.currencyexchanger.common.Response
import com.haroldcalayan.currencyexchanger.common.base.BaseViewModel
import com.haroldcalayan.currencyexchanger.common.util.DateUtil
import com.haroldcalayan.currencyexchanger.common.util.toRoundedDouble
import com.haroldcalayan.currencyexchanger.data.source.local.entity.CurrencyBalance
import com.haroldcalayan.currencyexchanger.data.source.remote.dto.ExchangeRatesDTO
import com.haroldcalayan.currencyexchanger.domain.use_case.GetCurrencyBalanceUseCase
import com.haroldcalayan.currencyexchanger.domain.use_case.GetExchangeRatesUseCase
import com.haroldcalayan.currencyexchanger.domain.use_case.SetCurrencyBalanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.concurrent.schedule

@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val getCurrencyBalanceUseCase: GetCurrencyBalanceUseCase,
    private val setCurrencyBalanceUseCase: SetCurrencyBalanceUseCase
) : BaseViewModel() {

    private val _exchangeRateState = mutableStateOf(DataState<ExchangeRatesDTO>())

    private val _currencyBalanceState = MutableStateFlow(DataState<List<CurrencyBalance>>())
    val currencyBalanceState = _currencyBalanceState.asStateFlow()

    private val _insertBalance = mutableStateOf(DataState<Unit>())

    private val _receiveData = MutableStateFlow(DEFAULT_CURRENCY_VALUE)
    val receiveData = _receiveData.asStateFlow()

    private val _sellData = MutableStateFlow(DEFAULT_CURRENCY_VALUE)
    val sellData = _sellData.asStateFlow()

    private val _sellCurrency = MutableStateFlow(BASE_CURRENCY)
    val sellCurrency = _sellCurrency.asStateFlow()

    private val _receiveCurrency = MutableStateFlow("USD")
    val receiveCurrency = _receiveCurrency.asStateFlow()

    private val _conversionMessage =
        MutableStateFlow(Triple(NO_CURRENCY_VALUE, NO_CURRENCY_VALUE, NO_CURRENCY_VALUE))
    val conversionMessage = _conversionMessage.asStateFlow()

    private val _isErrorConversionMessage = MutableStateFlow(false)
    val isErrorConversionMessage = _isErrorConversionMessage.asStateFlow()

    private val _freeTrial = MutableStateFlow(FREE_TRIAL)
    val freeTrial = _freeTrial.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun startLoading() {
        _isLoading.value = true
    }

    private var isInitialBalance = false

    private var timerTask: TimerTask? = null

    fun setSellCurrency(position: Int) {
        _sellCurrency.value =
            currencyBalanceState.value.data?.sortedByDescending { item -> item.cashInDate }
                ?.sortedByDescending { item -> item.isBase }?.filter {
                    it.balance != 0.0 && it.balance != null
                }?.get(position)?.currencyName.toString()
    }

    fun setReceiveCurrency(position: Int) {
        _receiveCurrency.value =
            currencyBalanceState.value.data?.sortedByDescending { item -> item.cashInDate }
                ?.sortedByDescending { item -> item.isBase }?.get(position)?.currencyName.toString()
    }

    fun getExchangeRates() {
        timerTask = Timer("AutoRefreshApi", false).schedule(5000) {
            getExchangeRatesUseCase().onEach { result ->
                when (result) {
                    is Response.Success -> {
                        _exchangeRateState.value = DataState(data = result.data)

                        getCurrencyBalance()
                    }
                }
            }.launchIn(viewModelScope)
            getExchangeRates()
        }
    }

    fun getCurrencyBalance() {
        getCurrencyBalanceUseCase().onEach { result ->
            when (result) {
                is Response.Success -> {
                    if (!result.data.isNullOrEmpty()) {
                        isInitialBalance = false
                        _currencyBalanceState.value = DataState(data = result.data)
                        _isLoading.value = false
                    } else {
                        isInitialBalance = true
                        setCurrencyBalance(_exchangeRateState.value.data, isInitialBalance)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun setCurrencyBalance(
        exchangeRate: ExchangeRatesDTO?,
        isInitialBalance: Boolean
    ) {
        val currencyBalance: MutableList<CurrencyBalance> = mutableListOf()
        exchangeRate?.rates?.forEach { currency ->
            val balance = if (isInitialBalance && currency.key == BASE_CURRENCY) INITIAL_BALANCE_VALUE else NO_CURRENCY_VALUE
            val date = if (isInitialBalance && currency.key == BASE_CURRENCY) DateUtil.getCurrentDate() else null
            val isBase = currency.key == BASE_CURRENCY
            currencyBalance.add(
                CurrencyBalance(
                    currencyName = currency.key,
                    balance = balance.toRoundedDouble(),
                    cashInDate = date,
                    isBase = isBase
                )
            )
        }

        insertNewBalance(currencyBalance)
    }


    private fun insertNewBalance(
        currencies: MutableList<CurrencyBalance>,
        sellDataValue: Double? = null,
        receiveValue: Double? = null,
        charge: Double? = null
    ) {
        setCurrencyBalanceUseCase(currencies).onEach { result ->
            when (result) {
                is Response.Success -> {
                    _insertBalance.value = DataState(data = result.data)
                    sellDataValue?.let {
                        _conversionMessage.value = Triple(
                            it,
                            receiveValue ?: NO_CURRENCY_VALUE,
                            charge ?: NO_CURRENCY_VALUE
                        )

                        setFreeTrial()
                    }

                }
            }
        }.launchIn(viewModelScope)
    }

    fun convertCurrency(sellData: String) {
        var sellValue = if (sellData.isNotBlank()) sellData.toDouble() else NO_CURRENCY_VALUE
        _sellData.value = sellValue.toString()

        if (sellCurrency.value != BASE_CURRENCY) {
            sellValue /= (_exchangeRateState.value.data?.rates?.get(sellCurrency.value)
                ?: NO_CURRENCY_VALUE)
        }

        val currencyValue = _exchangeRateState.value.data?.rates?.get(receiveCurrency.value)
        val receiveDataRounded = (sellValue * (currencyValue ?: NO_CURRENCY_VALUE))
        _receiveData.value = receiveDataRounded.toString()
    }

    fun submitConversion() {
        val oldSellBalance =
            currencyBalanceState.value.data?.find { it.currencyName == sellCurrency.value }?.balance
        val sellValue = sellData.value.toDouble()
        val charge = getCharge(sellValue)
        val newSellBalance = oldSellBalance?.minus(sellValue.plus(charge))
        val currencyBalance =
            currencyBalanceState.value.data?.find { it.currencyName == receiveCurrency.value }
        val totalCurrencyBalance =
            receiveData.value.toDouble().plus((currencyBalance?.balance ?: NO_CURRENCY_VALUE))

        val convertedCurrency = getNewConversionRates(newSellBalance, totalCurrencyBalance)

        if ((newSellBalance ?: NO_CURRENCY_VALUE) > NO_CURRENCY_VALUE) {
            insertNewBalance(
                convertedCurrency.toMutableList(), sellValue,
                receiveData.value.toDouble(), charge
            )
        } else {
            _isErrorConversionMessage.value = true
        }
    }

    private fun getNewConversionRates(
        sellValue: Double?,
        receiveValue: Double?
    ): List<CurrencyBalance> {
        return listOf(
            CurrencyBalance(
                currencyName = sellCurrency.value,
                balance = sellValue?.toRoundedDouble(),
                cashInDate = DateUtil.getCurrentDate(),
                isBase = sellCurrency.value == BASE_CURRENCY
            ),
            CurrencyBalance(
                currencyName = receiveCurrency.value,
                balance = receiveValue?.toRoundedDouble(),
                cashInDate = DateUtil.getCurrentDate(),
                isBase = receiveCurrency.value == BASE_CURRENCY
            )
        )
    }

    private fun getCharge(sellValue: Double): Double {
        return if ((sellValue <= MINIMUM_ENTRY && sellCurrency.value == BASE_CURRENCY) || freeTrial.value > 0) {
            NO_CURRENCY_VALUE
        } else {
            sellValue.times(
                CURRENT_COMMISSION
            )
        }
    }

    fun reAssignStates() {
        _sellData.value = DEFAULT_CURRENCY_VALUE
        _receiveData.value = DEFAULT_CURRENCY_VALUE
        _conversionMessage.value = Triple(NO_CURRENCY_VALUE, NO_CURRENCY_VALUE, NO_CURRENCY_VALUE)
        _isErrorConversionMessage.value = false
    }

    private fun setFreeTrial() {
        if (freeTrial.value > 0) {
            _freeTrial.value = _freeTrial.value - 1
        }
    }

    fun clearTask() {
        timerTask?.cancel()
    }
}