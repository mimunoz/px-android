package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.datasource.mapper.FromPayerPaymentMethodToCardMapper
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.features.payment_result.remedies.AlternativePayerPaymentMethodsMapper
import com.mercadopago.android.px.internal.mappers.*
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorMapper
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper

internal object MapperProvider {
    fun getPaymentMethodDrawableItemMapper(): PaymentMethodDrawableItemMapper {
        val session = Session.getInstance()
        return PaymentMethodDrawableItemMapper(
            session.configurationModule.chargeRepository,
            session.configurationModule.disabledPaymentMethodRepository,
            session.configurationModule.applicationSelectionRepository,
            CardUiMapper,
            CardDrawerCustomViewModelMapper,
            session.payerPaymentMethodRepository,
            session.modalRepository
        )
    }

    fun getPaymentMethodDescriptorMapper(): PaymentMethodDescriptorMapper {
        val session = Session.getInstance()
        return PaymentMethodDescriptorMapper(
            session.configurationModule.paymentSettings,
            session.amountConfigurationRepository,
            session.configurationModule.disabledPaymentMethodRepository,
            session.configurationModule.applicationSelectionRepository,
            session.amountRepository
        )
    }

    fun getPaymentCongratsMapper(): PaymentCongratsModelMapper {
        return PaymentCongratsModelMapper(
            Session.getInstance().configurationModule.paymentSettings,
            Session.getInstance().configurationModule.trackingRepository
        )
    }

    fun getAmountDescriptorMapper(): AmountDescriptorMapper {
        return AmountDescriptorMapper(
            Session.getInstance().experimentsRepository
        )
    }

    fun getPostPaymentUrlsMapper() = PostPaymentUrlsMapper

    fun getAlternativePayerPaymentMethodsMapper(): AlternativePayerPaymentMethodsMapper {
        return AlternativePayerPaymentMethodsMapper(
            Session.getInstance().mercadoPagoESC,
            Session.getInstance().payerPaymentMethodRepository,
            Session.getInstance().paymentMethodRepository
        )
    }

    fun getFromPayerPaymentMethodToCardMapper(): FromPayerPaymentMethodToCardMapper {
        return FromPayerPaymentMethodToCardMapper(
            Session.getInstance().payerPaymentMethodRepository,
            Session.getInstance().paymentMethodRepository
        )
    }

    fun getPaymentMethodMapper(): PaymentMethodMapper {
        return PaymentMethodMapper(Session.getInstance().paymentMethodRepository)
    }

    fun getSummaryInfoMapper(): SummaryInfoMapper {
        return SummaryInfoMapper()
    }

    fun getElementDescriptorMapper(): ElementDescriptorMapper {
        return ElementDescriptorMapper()
    }

    fun getSummaryDetailDescriptorMapper(): SummaryDetailDescriptorMapper {
        val session = Session.getInstance()
        val paymentSettings = session.configurationModule.paymentSettings
        return SummaryDetailDescriptorMapper(
            session.amountRepository,
            getSummaryInfoMapper().map(paymentSettings.checkoutPreference!!),
            paymentSettings.currency,
            getAmountDescriptorMapper()
        )
    }
}