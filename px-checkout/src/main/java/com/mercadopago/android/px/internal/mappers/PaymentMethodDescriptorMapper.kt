package com.mercadopago.android.px.internal.mappers

import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView
import com.mercadopago.android.px.internal.viewmodel.*
import com.mercadopago.android.px.model.AmountConfiguration
import com.mercadopago.android.px.model.PayerCost
import com.mercadopago.android.px.model.PaymentTypes.*
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorModelByApplication as Model

internal class PaymentMethodDescriptorMapper(
    private val paymentSettings: PaymentSettingRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    private val applicationSelectionRepository: ApplicationSelectionRepository,
    private val amountRepository: AmountRepository) : Mapper<OneTapItem, Model>() {

    override fun map(value: OneTapItem): Model {
        val currency = paymentSettings.currency
        val customOptionId = value.customOptionId

        return Model(applicationSelectionRepository[customOptionId].paymentMethod.type).also { model ->
            value.getApplications().forEach { application ->
                val paymentTypeId = application.paymentMethod.type
                val payerPaymentMethodKey = PayerPaymentMethodKey(customOptionId, paymentTypeId)

                model[application] = when {
                    disabledPaymentMethodRepository.hasKey(payerPaymentMethodKey) ->
                        DisabledPaymentMethodDescriptorModel.createFrom(value.status.mainMessage)
                    isCreditCardPaymentType(paymentTypeId) || value.isConsumerCredits ->
                        amountConfigurationRepository.getConfigurationFor(payerPaymentMethodKey)?.let {
                            mapCredit(value, it)
                        }
                    isCardPaymentType(paymentTypeId) ->
                        amountConfigurationRepository.getConfigurationFor(payerPaymentMethodKey)?.let {
                            DebitCardDescriptorModel.createFrom(currency, it)
                        }
                    isAccountMoney(value.paymentMethodId) ->
                        AccountMoneyDescriptorModel.createFrom(value.accountMoney, currency,
                            amountRepository.getAmountToPay(value.paymentTypeId, null as PayerCost?))
                    else -> EmptyInstallmentsDescriptorModel.create()
                } ?: EmptyInstallmentsDescriptorModel.create()
            }
        }
    }

    private fun mapCredit(oneTapItem: OneTapItem, amountConfiguration: AmountConfiguration)
        : PaymentMethodDescriptorView.Model {
        //This model is useful for Credit Card and Consumer Credits
        // FIXME change model to represent more than just credit cards.
        val installmentsRightHeader = if (oneTapItem.hasBenefits()) oneTapItem.benefits.installmentsHeader else null
        val interestFree = if (oneTapItem.hasBenefits()) oneTapItem.benefits.interestFree else null
        return CreditCardDescriptorModel
            .createFrom(paymentSettings.currency, installmentsRightHeader, interestFree, amountConfiguration)
    }

}