package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.mappers.NonNullMapper
import com.mercadopago.android.px.internal.model.EscStatus
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository
import com.mercadopago.android.px.model.CustomSearchItem
import com.mercadopago.android.px.model.internal.remedies.Installment
import com.mercadopago.android.px.model.internal.remedies.RemedyPaymentMethod

internal class AlternativePayerPaymentMethodsMapper(
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val paymentMethodRepository: PaymentMethodRepository) :
    NonNullMapper<CustomSearchItem, RemedyPaymentMethod>() {

    override fun map(value: CustomSearchItem): RemedyPaymentMethod? {
        return paymentMethodRepository.value.find { it.id == value.paymentMethodId }?.let { paymentMethod ->
            RemedyPaymentMethod(
                value.id,
                null,
                value.issuer?.name,
                value.firstSixDigits,
                value.lastFourDigits,
                value.paymentMethodId,
                value.type,
                paymentMethod.securityCode?.length,
                paymentMethod.securityCode?.cardLocation,
                null,
                value.getAmountConfiguration(value.defaultAmountConfiguration!!)
                    .payerCosts
                    .map { payerCost -> Installment(payerCost.installments, payerCost.totalAmount) },
                value.escStatus ?: EscStatus.NOT_AVAILABLE,
                escManagerBehaviour.escCardIds.contains(value.id))
        }
    }
}
