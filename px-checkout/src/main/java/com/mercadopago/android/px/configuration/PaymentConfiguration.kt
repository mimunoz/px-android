package com.mercadopago.android.px.configuration

import android.os.Parcel
import android.os.Parcelable
import com.mercadopago.android.px.core.PaymentMethodPlugin
import com.mercadopago.android.px.core.PaymentProcessor
import com.mercadopago.android.px.core.ScheduledPaymentProcessor
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.core.internal.CheckoutDataMapper
import com.mercadopago.android.px.core.internal.NoOpPaymentProcessor
import com.mercadopago.android.px.core.internal.PaymentListenerMapper
import com.mercadopago.android.px.core.internal.PaymentProcessorMapper
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.internal.CheckoutType
import java.util.*
import com.mercadopago.android.px.core.v2.PaymentProcessor as PaymentProcessorV2

class PaymentConfiguration private constructor(val charges: ArrayList<PaymentTypeChargeRule>,
    val paymentProcessor: SplitPaymentProcessor,
    internal val paymentProcessorV2: PaymentProcessorV2) : Parcelable {

    private constructor(builder: Builder): this(
        builder.charges, builder.paymentProcessor, builder.paymentProcessorV2
    )

    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(PaymentTypeChargeRule.CREATOR)!!,
        parcel.readParcelable(SplitPaymentProcessor::class.java.classLoader)!!,
        parcel.readParcelable(PaymentProcessorV2::class.java.classLoader)!!
    )

    @Deprecated("")
    val discountConfiguration:DiscountConfiguration? = null

    @Deprecated("")
    val paymentMethodPluginList:Collection<PaymentMethodPlugin> = ArrayList()

    internal fun getCheckoutType(): CheckoutType {
        return when(paymentProcessorV2) {
            is ScheduledPaymentProcessor -> CheckoutType.SCHEDULED
            else -> CheckoutType.REGULAR
        }
    }

    class Builder {
        internal val paymentProcessorV2: PaymentProcessorV2
        val paymentProcessor: SplitPaymentProcessor
        val charges: ArrayList<PaymentTypeChargeRule>

        /**
         * @param paymentProcessor your custom payment processor.
         */
        constructor(paymentProcessor: PaymentProcessorV2) {
            this.paymentProcessor = NoOpPaymentProcessor()
            this.paymentProcessorV2 = paymentProcessor
            charges = ArrayList()
        }

        /**
         * @param paymentProcessor your custom payment processor.
         */
        constructor(paymentProcessor: SplitPaymentProcessor) {
            this.paymentProcessor = paymentProcessor
            this.paymentProcessorV2 = paymentProcessor
            charges = ArrayList()
        }

        /**
         * @param paymentProcessor your custom payment processor.
         */
        @Deprecated("you should migrate to split payment processor")
        constructor(paymentProcessor: PaymentProcessor) : this(
            PaymentProcessorMapper(PaymentListenerMapper(), CheckoutDataMapper()).map(paymentProcessor)
        )

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charges the list of charges that could apply.
         * @return builder to keep operating
         */
        fun addChargeRules(charges: Collection<PaymentTypeChargeRule>) = apply {
            this.charges.addAll(charges)
        }

        fun build() = PaymentConfiguration(this)

        /**
         * Add your own payment method option to pay. Deprecated on version 4.5.0 due to native support of account money
         * feature. This method is now NOOP.
         *
         * @param paymentMethodPlugin your payment method plugin.
         * @return builder
         */
        @Deprecated("this configuration is not longuer valid - NOOP method.")
        fun addPaymentMethodPlugin(paymentMethodPlugin: PaymentMethodPlugin) = this

        /**
         * [DiscountConfiguration] is an object that represents the discount to be applied or error information to
         * present to the user.
         *
         *
         * it's mandatory to handle your discounts by hand if you set a payment processor.
         *
         * @param discountConfiguration your custom discount configuration
         * @return builder to keep operating
         */
        @Deprecated("this configuration is not longer valid - NOOP method")
        fun setDiscountConfiguration(discountConfiguration: DiscountConfiguration) = this
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(charges)
        parcel.writeParcelable(paymentProcessor, flags)
        parcel.writeParcelable(paymentProcessorV2, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<PaymentConfiguration> {
        override fun createFromParcel(parcel: Parcel) = PaymentConfiguration(parcel)
        override fun newArray(size: Int) = arrayOfNulls<PaymentConfiguration>(size)
    }
}
