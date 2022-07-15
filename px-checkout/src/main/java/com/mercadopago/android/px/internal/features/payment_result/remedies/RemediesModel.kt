package com.mercadopago.android.px.internal.features.payment_result.remedies

import android.os.Parcel
import android.os.Parcelable
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.RetryPaymentFragment
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.HighRiskRemedy
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType

internal data class RemediesModel(val title: String, val retryPayment: RetryPaymentFragment.Model?,
    val highRisk: HighRiskRemedy.Model?, val trackingData: Map<String, String>?) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!,
        parcel.readParcelable(RetryPaymentFragment.Model::class.java.classLoader),
        parcel.readParcelable(HighRiskRemedy.Model::class.java.classLoader),
        HashMap()) {
        if (trackingData != null) {
            parcel.readMap(trackingData, String::class.java.classLoader)
        }
    }

    fun hasRemedies() = retryPayment != null || highRisk != null

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeParcelable(retryPayment, flags)
        parcel.writeParcelable(highRisk, flags)
        parcel.writeMap(trackingData)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField val DECORATOR = PaymentResultType.PENDING
        @JvmField val CREATOR = object : Parcelable.Creator<RemediesModel> {
            override fun createFromParcel(parcel: Parcel) = RemediesModel(parcel)
            override fun newArray(size: Int) = arrayOfNulls<RemediesModel?>(size)
        }
    }
}
