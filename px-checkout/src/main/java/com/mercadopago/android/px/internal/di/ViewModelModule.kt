package com.mercadopago.android.px.internal.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class ViewModelModule {
    private val factory = ViewModelFactory()

    fun <T : ViewModel?> get(fragment: Fragment, modelClass: Class<T>): T {
        return ViewModelProvider(fragment, factory).get(modelClass)
    }
}
