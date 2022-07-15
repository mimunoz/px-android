package com.mercadopago.android.px.addons

import android.app.Activity
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.addons.internal.SecurityDefaultBehaviour
import com.mercadopago.android.px.addons.internal.SecurityValidationHandler
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.addons.validator.SecurityValidator

class MockSecurityBehaviour(private val escManagerBehaviour: ESCManagerBehaviour) : SecurityBehaviour {

    override fun isSecurityEnabled(data: SecurityValidationData) = false

    override fun startValidation(activity: Activity, data: SecurityValidationData, requestCode: Int) {
        if (activity is SecurityValidationHandler) {
            if (SecurityValidator(escManagerBehaviour).validate(data)) {
                MockFingerprintActivity.start(activity, requestCode)
            } else {
                SecurityDefaultBehaviour().startValidation(activity, data, requestCode)
            }
        } else {
            throw RuntimeException("Activity must implement SecurityValidationHandler")
        }
    }

    override fun startValidation(fragment: Fragment, data: SecurityValidationData, requestCode: Int) {
        if (fragment is SecurityValidationHandler) {
            if (SecurityValidator(escManagerBehaviour).validate(data)) {
                MockFingerprintActivity.start(fragment, requestCode)
            } else {
                SecurityDefaultBehaviour().startValidation(fragment, data, requestCode)
            }
        } else {
            throw RuntimeException("Fragment must implement SecurityValidationHandler")
        }
    }

    override fun getExtraResultKey() = VALIDATED_SCREEN_LOCK

    companion object {
        private const val VALIDATED_SCREEN_LOCK = "VALIDATED_SCREEN_LOCK"
    }
}