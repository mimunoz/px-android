package com.mercadopago.android.px.addons.internal

import android.app.Activity
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.addons.SecurityBehaviour
import com.mercadopago.android.px.addons.model.SecurityValidationData

class SecurityDefaultBehaviour : SecurityBehaviour {

    override fun isSecurityEnabled(data: SecurityValidationData) = false

    override fun startValidation(activity: Activity, data: SecurityValidationData, requestCode: Int) {
        if (activity is SecurityValidationHandler) {
            activity.onSecurityValidated()
        } else {
            throw RuntimeException("Activity must implement SecurityValidationHandler")
        }
    }

    override fun startValidation(fragment: Fragment, data: SecurityValidationData, requestCode: Int) {
        if (fragment is SecurityValidationHandler) {
            fragment.onSecurityValidated()
        } else {
            throw RuntimeException("Fragment must implement SecurityValidationHandler")
        }
    }

    override fun getExtraResultKey() = VALIDATED_SCREEN_LOCK

    companion object {
        private const val VALIDATED_SCREEN_LOCK = "VALIDATED_SCREEN_LOCK"
    }
}