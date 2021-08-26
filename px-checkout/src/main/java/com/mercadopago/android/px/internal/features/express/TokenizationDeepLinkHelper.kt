package com.mercadopago.android.px.internal.features.express

import androidx.fragment.app.Fragment
import com.mercadolibre.android.andesui.snackbar.type.AndesSnackbarType
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.callbacks.TokenizationResponse
import com.mercadopago.android.px.internal.callbacks.TokenizationResponse.State.SUCCESS
import com.mercadopago.android.px.internal.callbacks.TokenizationResponse.State.PENDING
import com.mercadopago.android.px.internal.callbacks.TokenizationResponse.State.ERROR
import com.mercadopago.android.px.internal.extensions.showSnackBar

internal object TokenizationDeepLinkHelper {

    @JvmStatic
    fun doAction(tokenizationResponse: TokenizationResponse, fragment: Fragment) {
        with (fragment.resources) {
            when (tokenizationResponse.result) {
                SUCCESS -> showSnackBar(getString(R.string.px_tokenization_snackbar_success), AndesSnackbarType.SUCCESS, fragment)
                PENDING -> showSnackBar(getString(R.string.px_tokenization_snackbar_pending), AndesSnackbarType.NEUTRAL, fragment)
                ERROR -> showSnackBar(getString(R.string.px_tokenization_snackbar_error), AndesSnackbarType.ERROR, fragment)
            }
        }
    }

    private fun showSnackBar(message: String, andesSnackbarType: AndesSnackbarType, fragment: Fragment) {
        with(fragment) {
            view?.showSnackBar(message, andesSnackbarType)
        }
    }
}
