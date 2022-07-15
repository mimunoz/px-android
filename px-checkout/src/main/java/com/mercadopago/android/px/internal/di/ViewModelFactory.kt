package com.mercadopago.android.px.internal.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.pay_button.PayButtonViewModel

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayButtonViewModel::class.java)) {
            return PayButtonViewModel(Session.getInstance().paymentRepository,
                Session.getInstance().networkModule.productIdProvider,
                ConnectionHelper.instance,
                Session.getInstance().configurationModule.paymentSettings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
