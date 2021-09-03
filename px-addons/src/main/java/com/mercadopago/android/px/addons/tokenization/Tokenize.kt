package com.mercadopago.android.px.addons.tokenization

import android.app.Activity
import androidx.fragment.app.Fragment

open class Tokenize {

    protected var sessionId: String? = null
    protected var showLoading: Boolean? = null

    fun setSessionId(sessionId: String) = apply {
        this.sessionId = sessionId
    }
    fun showLoading(showLoading: Boolean) = apply {
        this.showLoading = showLoading
    }
    open fun start(fragment: Fragment, requestCode: Int) {
        DummyTokenizationActivity.start(fragment, requestCode)
    }
    open fun start(activity: Activity, requestCode: Int) {
        DummyTokenizationActivity.start(activity, requestCode)
    }
}
