package com.haroldcalayan.currencyexchanger.presentation.ui.currency_exchange

import android.app.Dialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.haroldcalayan.currencyexchanger.R
import com.haroldcalayan.currencyexchanger.common.Constants.DEFAULT_CURRENCY_VALUE
import com.haroldcalayan.currencyexchanger.common.base.BaseActivity
import com.haroldcalayan.currencyexchanger.common.util.AlertDialogHelper
import com.haroldcalayan.currencyexchanger.common.util.ProgressDialogHelper
import com.haroldcalayan.currencyexchanger.common.util.clear
import com.haroldcalayan.currencyexchanger.common.util.toRoundedDecimalString
import com.haroldcalayan.currencyexchanger.common.util.toRoundedDouble
import com.haroldcalayan.currencyexchanger.databinding.ActivityCurrencyExchangeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CurrencyExchangeActivity :
    BaseActivity<CurrencyExchangeViewModel, ActivityCurrencyExchangeBinding>() {
    override val layoutId = R.layout.activity_currency_exchange
    override val viewModel: CurrencyExchangeViewModel by viewModels()

    private lateinit var currencyBalanceAdapter: CurrencyBalanceAdapter
    private lateinit var progressDialog: Dialog

    val context = this

    override fun initViews() {
        super.initViews()

        viewModel.getExchangeRates()

        initCurrencyBalanceList()

        progressDialog = ProgressDialogHelper.progressDialog(this)
        viewModel.startLoading()

        binding.edittextInputSell.setOnClickListener {
            binding.edittextInputSell.selectAll()
        }

        binding.edittextInputSell.doOnTextChanged { text, _, _, _ ->

            if (binding.textViewCurrency.text.toString() != binding.textViewCurrencyReceive.text.toString()) {
                viewModel.convertCurrency(text.toString())
            } else {
                val receiveValue = if (text.toString().isNotBlank()) {
                    binding.textviewValueReceive.setTextColor(
                        ContextCompat.getColorStateList(context, R.color.green)
                    )
                    "+${text.toString()}"
                } else {
                    binding.textviewValueReceive.setTextColor(
                        ContextCompat.getColorStateList(context, R.color.gray)
                    )
                    DEFAULT_CURRENCY_VALUE
                }
                binding.textviewValueReceive.text = receiveValue
            }

            viewModel.convertCurrency(text.toString())
            binding.buttonSubmit.isEnabled = text.toString().isNotBlank()

            if (binding.buttonSubmit.isEnabled) {
                binding.buttonSubmit.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primaryDarkColor)
            } else binding.buttonSubmit.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.gray)
        }

        binding.textViewCurrency.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.setSellCurrency(position)
                viewModel.convertCurrency(binding.edittextInputSell.text.toString())
            }

        binding.textViewCurrencyReceive.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.setReceiveCurrency(position)
                viewModel.convertCurrency(binding.edittextInputSell.text.toString())
            }

        binding.buttonSubmit.setOnClickListener {
            if (binding.textViewCurrency.text.toString() != binding.textViewCurrencyReceive.text.toString()) {
                viewModel.submitConversion()
            } else {
                val message = String.format(
                    getString(R.string.convertion_invalid_message),
                    viewModel.sellCurrency.value,
                    viewModel.receiveCurrency.value,
                )
                displayFailedConvertAlertDialog(
                    getString(R.string.error_invalid_conversion),
                    message
                )
            }
        }
    }

    override fun subscribe() {
        super.subscribe()

        lifecycleScope.launchWhenCreated {
            viewModel.currencyBalanceState.collectLatest {
                it.data?.let { currencyBalance ->
                    currencyBalanceAdapter.updateData(currencyBalance)
                    val currenciesSeller: MutableList<String> = mutableListOf()
                    val currenciesReceiver: MutableList<String> = mutableListOf()
                    currencyBalance.sortedByDescending { item -> item.cashInDate }
                        .sortedByDescending { item -> item.isBase }.forEach { item ->
                            currenciesReceiver.add(item.currencyName.orEmpty())
                            if (item.balance != 0.0 && item.balance != null) {
                                currenciesSeller.add(item.currencyName.orEmpty())
                            }
                        }
                    val optionsSeller =
                        ArrayAdapter(context, R.layout.dropdown_text, currenciesSeller)
                    val optionsReceiver =
                        ArrayAdapter(context, R.layout.dropdown_text, currenciesReceiver)

                    binding.textViewCurrency.setAdapter(optionsSeller)
                    binding.textViewCurrencyReceive.setAdapter(optionsReceiver)

                    binding.textViewCurrency.setOnClickListener {
                        binding.textViewCurrency.showDropDown()
                        context.hideKeyboard()
                    }

                    binding.textViewCurrencyReceive.setOnClickListener {
                        binding.textViewCurrencyReceive.showDropDown()
                        hideKeyboard()
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.receiveData.collectLatest {
                it.let {
                    val number = it.toRoundedDecimalString()
                    if (it.toDouble() > 0.00) {
                        binding.textviewValueReceive.setTextColor(
                            ContextCompat.getColorStateList(
                                context,
                                R.color.green
                            )
                        )
                        binding.textviewValueReceive.text = "+$number"
                    } else if ((it.toDouble() == 0.00)) {
                        binding.textviewValueReceive.setTextColor(
                            ContextCompat.getColorStateList(context, R.color.gray)
                        )
                        binding.textviewValueReceive.text = "$number"
                    } else {
                        binding.textviewValueReceive.setTextColor(
                            ContextCompat.getColorStateList(context, R.color.red)
                        )
                        binding.textviewValueReceive.text = "-$number"
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.conversionMessage.collect {
                binding.edittextInputSell.clear()
                binding.buttonSubmit.isEnabled = false
                if (it.first != 0.00) displayConvertAlertDialog(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.isErrorConversionMessage.collectLatest {
                binding.edittextInputSell.clear()
                if (it) displayFailedConvertAlertDialog(
                    getString(R.string.convesion_message_insufficient_balance),
                    getString(R.string.conversion_error_message, viewModel.sellCurrency.value)
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.isLoading.collectLatest {
                if (it) {
                    progressDialog.show()
                } else {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearTask()
    }

    private fun initCurrencyBalanceList() {
        currencyBalanceAdapter = CurrencyBalanceAdapter(emptyList())
        binding.recyclerCurrencyBalance.adapter = currencyBalanceAdapter
    }

    private fun displayConvertAlertDialog(data: Triple<Double, Double, Double>) {
        val alert = AlertDialogHelper(this)
        val message = String.format(
            getString(R.string.conversion_success_message),
            (data.first.toRoundedDouble()).toString(),
            viewModel.sellCurrency.value,
            data.second.toString().toRoundedDecimalString(),
            viewModel.receiveCurrency.value,
            (data.third.toRoundedDouble()).toString(),
            viewModel.sellCurrency.value
        )

        alert.alertDialogHelperTwoButtonsListener =
            object : AlertDialogHelper.AlertDialogHelperTwoButtonsListener {
                override fun onPositiveButtonClicked() {
                    viewModel.getCurrencyBalance()
                    viewModel.reAssignStates()
                    Toast.makeText(context, getString(R.string.text_saved), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        alert.displayAlertDialogTwoButtons(
            getString(R.string.title_currency_converted),
            message,
            getString(R.string.neutral_button_done)
        )
    }

    private fun displayFailedConvertAlertDialog(title: String, message: String) {
        val alert = AlertDialogHelper(this)

        alert.alertDialogHelperTwoButtonsListener =
            object : AlertDialogHelper.AlertDialogHelperTwoButtonsListener {
                override fun onPositiveButtonClicked() {
                    viewModel.reAssignStates()
                }
            }

        alert.displayAlertDialogTwoButtons(
            title,
            message,
            getString(R.string.conversion_neutral_button)
        )
    }

    private fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.applicationWindowToken, 0)
    }
}