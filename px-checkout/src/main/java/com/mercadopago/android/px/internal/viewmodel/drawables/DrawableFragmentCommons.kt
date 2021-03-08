package com.mercadopago.android.px.internal.viewmodel.drawables

import android.os.Parcelable
import com.mercadopago.android.px.model.StatusMetadata
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class DrawableFragmentCommons(
    val status: StatusMetadata,
    val chargeMessage: String?,
    val disabledPaymentMethod: DisabledPaymentMethod?
) : Parcelable {

    @Parcelize
    internal data class ByApplication(private var defaultKey: String,
        private val values: HashMap<String, DrawableFragmentCommons> = hashMapOf()) : Parcelable {

        fun update(paymentTypeId: String) {
            defaultKey = paymentTypeId
        }

        operator fun set(application: Application?, model: DrawableFragmentCommons) {
            values[application?.paymentMethod?.type.orEmpty()] = model
        }

        fun getCurrent(): DrawableFragmentCommons {
            return values[defaultKey] ?: throw IllegalStateException("There is no model for current application")
        }
    }
}