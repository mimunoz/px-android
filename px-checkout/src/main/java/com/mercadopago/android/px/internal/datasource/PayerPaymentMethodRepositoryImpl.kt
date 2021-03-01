package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.model.CustomSearchItem
import java.io.File

private const val PAYER_PAYMENT_METHODS = "payer_payment_methods_repository"

internal class PayerPaymentMethodRepositoryImpl(
    private val fileManager: FileManager,
    private val applicationSelectionRepository: ApplicationSelectionRepository) :
    AbstractLocalRepository<List<CustomSearchItem>>(fileManager), PayerPaymentMethodRepository {

    override val file: File = fileManager.create(PAYER_PAYMENT_METHODS)

    override fun readFromStorage() = fileManager.readAnyList(file, CustomSearchItem::class.java)

    override fun get(key: PayerPaymentMethodRepository.Key): CustomSearchItem? {
        return value.firstOrNull {
            it.id == key.payerPaymentMethodId
                && it.type == key.paymentTypeId
        }
    }

    override fun get(customOptionId: String): CustomSearchItem? {
        val (paymentMethodId, paymentMethodTypeId) = applicationSelectionRepository[customOptionId]
            ?.paymentMethod
            ?.let { it.id to it.type }
            ?: customOptionId to null

        return value.firstOrNull {
            it.id == customOptionId
                && it.paymentMethodId == paymentMethodId
                && it.type == paymentMethodTypeId
        }
    }

    override fun getIdsWithSplitAllowed(): Set<String> {
        return mutableSetOf<String>().also { map ->
            value.map { payerPaymentMethod ->
                payerPaymentMethod.getAmountConfiguration(payerPaymentMethod.defaultAmountConfiguration)
                    .takeIf { it.allowSplit() }?.run { map.add(payerPaymentMethod.id) }
            }
        }
    }
}