package com.mercadopago.android.px.di.module

import androidx.lifecycle.ViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.mercadopago.android.px.di.ViewModelFactory

internal class ViewModelModule {
    private val factory = ViewModelFactory()

    fun <T : ViewModel?> get(fragment: Fragment, modelClass: Class<T>): T {
        return ViewModelProvider(fragment, factory).get(modelClass)
    }

    fun <T : ViewModel?> get(activity: FragmentActivity, modelClass: Class<T>): T {
        return ViewModelProvider(activity, factory).get(modelClass)
    }
}
