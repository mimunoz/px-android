package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.model.AmountConfiguration

internal class ConfigurationSolverImpl(
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository) : ConfigurationSolver {

    override fun getConfigurationHashFor(customOptionId: String): String {
        return payerPaymentMethodRepository.value.firstOrNull { it.id.equals(customOptionId, ignoreCase = true) }
            ?.defaultAmountConfiguration ?: TextUtil.EMPTY
    }

    override fun getAmountConfigurationFor(customOptionId: String): AmountConfiguration? {
        return payerPaymentMethodRepository.value.firstOrNull { it.id.equals(customOptionId, ignoreCase = true) }
            ?.let { it.getAmountConfiguration(it.defaultAmountConfiguration) }
    }
}