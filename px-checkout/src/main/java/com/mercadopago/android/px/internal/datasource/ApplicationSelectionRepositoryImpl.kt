package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.internal.repository.PayerPaymentTypeId
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.OneTapItem
import java.io.File

private const val SELECTED_APPLICATIONS = "selected_applications_repository"

internal class ApplicationSelectionRepositoryImpl(private val fileManager: FileManager,
    private val oneTapItemRepository: OneTapItemRepository) :
    AbstractLocalRepository<MutableMap<PayerPaymentTypeId, Application>>(fileManager), ApplicationSelectionRepository {

    override val file: File = fileManager.create(SELECTED_APPLICATIONS)

    override fun get(payerPaymentMethodId: PayerPaymentTypeId): Application? {
        val selectedApplication = value[payerPaymentMethodId]
        return selectedApplication ?: resolveDefault(payerPaymentMethodId)
    }

    private fun resolveDefault(payerPaymentMethodId: PayerPaymentTypeId): Application? {
        return oneTapItemRepository.value.firstOrNull { it.customOptionId == payerPaymentMethodId }
            ?.let { oneTapItem ->
                oneTapItem
                    .getApplications()
                    ?.let { getApplication(oneTapItem, it) }
                    ?.also { set(payerPaymentMethodId, it) }
            }
    }

    private fun getApplication(oneTapItem: OneTapItem, applications: List<Application>): Application? {
        return if (applications.size == 1) applications.first() else applications.firstOrNull { application ->
            oneTapItem.displayInfo?.cardDrawerSwitch?.default == application.paymentMethod.type
        }
    }

    override fun set(payerPaymentMethodId: PayerPaymentTypeId, application: Application) {
        value[payerPaymentMethodId] = application
        configure(value)
    }

    override fun readFromStorage() = fileManager
        .readAnyMap(file, PayerPaymentTypeId::class.java, Application::class.java)
        as MutableMap<PayerPaymentTypeId, Application>
}
