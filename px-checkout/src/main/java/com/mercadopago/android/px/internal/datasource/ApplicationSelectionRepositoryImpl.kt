package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository.ApplicationSelection
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.model.internal.Application
import java.io.File

private const val SELECTED_APPLICATIONS = "selected_applications_repository"

internal class ApplicationSelectionRepositoryImpl(private val fileManager: FileManager,
    private val oneTapItemRepository: OneTapItemRepository) :
    AbstractLocalRepository<List<ApplicationSelection>>(fileManager), ApplicationSelectionRepository {

    override val file: File = fileManager.create(SELECTED_APPLICATIONS)

    override fun get(payerPaymentMethodId: String): Application? {
        val selectedApplication = value.firstOrNull { it.payerPaymentMethodId == payerPaymentMethodId }
        return selectedApplication?.application ?: resolveDefault(payerPaymentMethodId)
    }

    private fun resolveDefault(payerPaymentMethodId: String): Application? {
        return oneTapItemRepository.value.firstOrNull { it.customOptionId == payerPaymentMethodId }
            ?.let { oneTapItem ->
                oneTapItem.getApplications().firstOrNull {
                    oneTapItem.displayInfo?.cardDrawerSwitch?.default == it.paymentMethod.type
                }?.also { set(payerPaymentMethodId, it) }
            }
    }

    override fun set(payerPaymentMethodId: String, application: Application) {
        mutableListOf<ApplicationSelection>().also { list ->
            list.addAll(value.filter { it.payerPaymentMethodId == payerPaymentMethodId })
            list.add(ApplicationSelection(payerPaymentMethodId, application))
            configure(list)
        }
    }

    override fun readFromStorage() = fileManager.readAnyList(file, ApplicationSelection::class.java)
}
