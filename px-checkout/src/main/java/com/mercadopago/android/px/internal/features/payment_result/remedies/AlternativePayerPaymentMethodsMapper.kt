package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.mappers.NonNullMapper
import com.mercadopago.android.px.internal.model.EscStatus
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository
import com.mercadopago.android.px.model.ExpressMetadata
import com.mercadopago.android.px.model.internal.remedies.Installment
import com.mercadopago.android.px.model.internal.remedies.RemedyPaymentMethod

internal class AlternativePayerPaymentMethodsMapper(
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    private val paymentMethodRepository: PaymentMethodRepository) :
    NonNullMapper<ExpressMetadata, RemedyPaymentMethod>() {

    override fun map(value: ExpressMetadata): RemedyPaymentMethod? {
        return payerPaymentMethodRepository.value.find { it.id == value.customOptionId }?.let { payerPaymentMethod ->
            paymentMethodRepository.value.find { it.id == payerPaymentMethod.paymentMethodId }?.let { paymentMethod ->
                RemedyPaymentMethod(
                    payerPaymentMethod.id,
                    null,
                    payerPaymentMethod.issuer?.name,
                    payerPaymentMethod.lastFourDigits,
                    payerPaymentMethod.paymentMethodId,
                    payerPaymentMethod.type,
                    paymentMethod.securityCode?.length,
                    paymentMethod.securityCode?.cardLocation,
                    null,
                    payerPaymentMethod.getAmountConfiguration(payerPaymentMethod.defaultAmountConfiguration!!)
                        .payerCosts
                        .map { payerCost -> Installment(payerCost.installments, payerCost.totalAmount) },
                    payerPaymentMethod.escStatus ?: EscStatus.NOT_AVAILABLE,
                    escManagerBehaviour.escCardIds.contains(payerPaymentMethod.id))
            }
        }
    }
}
