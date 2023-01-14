package com.haroldcalayan.currencyexchanger.presentation.ui.splash

import androidx.lifecycle.viewModelScope
import com.haroldcalayan.currencyexchanger.common.DataState
import com.haroldcalayan.currencyexchanger.common.Response
import com.haroldcalayan.currencyexchanger.common.base.BaseViewModel
import com.haroldcalayan.currencyexchanger.domain.use_case.ResetDatasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val resetDatasUseCase: ResetDatasUseCase
) : BaseViewModel() {

    private val _resetState = MutableStateFlow(DataState<Unit>())
    val resetState = _resetState.asStateFlow()

    fun resetDataDB() {
        resetDatasUseCase().onEach { result ->
            when (result) {
                is Response.Success -> {
                    _resetState.value = DataState(data = result.data)
                }
            }
        }.launchIn(viewModelScope)
    }

}