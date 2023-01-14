package com.haroldcalayan.currencyexchanger.presentation.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.haroldcalayan.currencyexchanger.R
import com.haroldcalayan.currencyexchanger.common.Constants
import com.haroldcalayan.currencyexchanger.common.base.BaseActivity
import com.haroldcalayan.currencyexchanger.common.util.showToastShort
import com.haroldcalayan.currencyexchanger.databinding.ActivitySplashBinding
import com.haroldcalayan.currencyexchanger.presentation.ui.currency_exchange.CurrencyExchangeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>() {
    override val layoutId = R.layout.activity_splash
    override val viewModel: SplashViewModel by viewModels()

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun initViews() {
        super.initViews()
        hideSystemUI()
    }

    override fun subscribe() {
        super.subscribe()

        lifecycleScope.launchWhenCreated {
            viewModel.resetState.collectLatest {
                activityScope.launch {
                    delay(Constants.SPLASH_SCREEN_LIFE)
                    finish()
                    openMainScreen()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initPermissions()
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initPermissions()
                } else {
                    showToastShort(R.string.message_app_requires_permission)
                }
                return
            }
        }
    }

    private fun openMainScreen() {
        val intent = Intent(this, CurrencyExchangeActivity::class.java)
        startActivity(intent)
    }

    private fun initPermissions() {
        if (areAllPermissionsGranted()) {
            viewModel.resetDataDB()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissions = arrayOf(
                    Manifest.permission.INTERNET
                )
                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 10
    }

}