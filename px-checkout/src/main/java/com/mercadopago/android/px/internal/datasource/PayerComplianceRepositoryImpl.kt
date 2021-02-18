package com.mercadopago.android.px.internal.datasource

import android.content.SharedPreferences
import com.mercadopago.android.px.internal.core.FileManager
import com.mercadopago.android.px.internal.repository.PayerComplianceRepository
import com.mercadopago.android.px.model.PayerCompliance
import java.io.File

private const val TURNED_IFPE_COMPLIANT = "turned_ifpe_compliant"
private const val PAYER_COMPLIANCE = "payer_compliance_repository"

internal class PayerComplianceRepositoryImpl(private val sharedPreferences: SharedPreferences, private val fileManager: FileManager) :
    AbstractLocalRepository<PayerCompliance?>(fileManager), PayerComplianceRepository {

    override val file: File = fileManager.create(PAYER_COMPLIANCE)

    override fun turnIFPECompliant() = sharedPreferences.edit()
        .putBoolean(TURNED_IFPE_COMPLIANT, value?.ifpe?.isCompliant ?: false).apply()

    override fun turnedIFPECompliant() = sharedPreferences.getBoolean(TURNED_IFPE_COMPLIANT, false)

    override fun configure(value: PayerCompliance?) {
        super.configure(value)
        sharedPreferences.edit().putBoolean(TURNED_IFPE_COMPLIANT, true).apply()
    }

    override fun reset() {
        super.reset()
        sharedPreferences.edit().remove(TURNED_IFPE_COMPLIANT).apply()
    }

    override fun readFromStorage(): PayerCompliance? = fileManager.readParcelable(file, PayerCompliance.CREATOR)
}