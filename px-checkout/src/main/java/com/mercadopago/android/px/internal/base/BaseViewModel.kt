package com.mercadopago.android.px.internal.base

import android.os.Bundle
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    open fun recoverFromBundle(bundle: Bundle) = Unit
    open fun storeInBundle(bundle: Bundle) = Unit
}