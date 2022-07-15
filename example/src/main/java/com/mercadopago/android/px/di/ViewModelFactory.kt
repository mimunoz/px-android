package com.mercadopago.android.px.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.feature.custom_initialize.CustomInitializationViewModel

internal class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomInitializationViewModel::class.java)) {
            return CustomInitializationViewModel(Dependencies.instance.localPreferences!!.initializationDataPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
