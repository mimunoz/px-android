package com.mercadopago.android.px.internal.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

internal fun <T> LiveData<T>.nonNullObserve(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    observe(owner) {
        it?.let(observer)
    }
}
