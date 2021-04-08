package com.mercadopago.android.px.model.internal

import android.os.Parcel
import com.mercadopago.android.px.internal.core.extensions.orIfNullOrEmpty
import com.mercadopago.android.px.model.ExpressMetadata

class OneTapItem(parcel: Parcel?) : ExpressMetadata(parcel) {

    private var applications: List<Application>? = null

    init {
        throw UnsupportedOperationException("Parcelable implementation not available")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        throw UnsupportedOperationException("Parcelable implementation not available")
    }

    fun getApplications() = applications.orIfNullOrEmpty(mutableListOf<Application>().also {
        it.add(
            Application(Application.PaymentMethod(paymentMethodId.orEmpty(), paymentTypeId.orEmpty()), listOf(), status)
        )
    })

    fun getDefaultPaymentMethodType(): String = displayInfo
        ?.cardDrawerSwitch
        ?.default
        ?: paymentTypeId ?: paymentMethodId
}