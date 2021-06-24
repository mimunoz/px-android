package com.mercadopago.android.px.internal.tracking

import android.content.Context
import android.content.SharedPreferences
import com.mercadopago.android.px.addons.SecurityBehaviour
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.internal.core.ProductIdProvider

class TrackingRepositoryImpl(
    context: Context,
    private val sharedPreferences: SharedPreferences,
    private val securityBehaviour: SecurityBehaviour,
    private val productIdProvider: ProductIdProvider) : TrackingRepository {

    private val applicationContext: Context = context.applicationContext

    private var internalSessionId: String? = null
    override val sessionId: String
        get() {
            if (internalSessionId == null) {
                internalSessionId = sharedPreferences.getString(PREF_SESSION_ID, null)
            }
            return internalSessionId ?: DEFAULT_SESSION_ID
        }

    private val flowProvider = FlowProvider(sharedPreferences)
    private val legacyFlowProvider = LegacyFlowProvider(applicationContext)

    override val flowId: String
        get() = flowProvider.flowId ?: legacyFlowProvider.flowId ?: DEFAULT_FLOW_ID
    override val flowDetail: Map<String, Any>
        get() = flowProvider.flowDetail ?: legacyFlowProvider.flowDetail ?: emptyMap()

    override val deviceSecured: Boolean
        get() = securityBehaviour.isDeviceSecure(applicationContext)

    override var securityEnabled = false
        private set

    override fun configure(model: TrackingRepository.Model) {
        securityEnabled = securityBehaviour.isSecurityEnabled(SecurityValidationData.Builder(productIdProvider.productId).build())
        internalSessionId = model.sessionId
        with(sharedPreferences.edit()) {
            putString(PREF_SESSION_ID, internalSessionId)
            apply()
        }
        flowProvider.configure(model.flowId, model.flowDetail)
    }

    override fun reset() {
        internalSessionId = null
        with(sharedPreferences.edit()) {
            remove(PREF_SESSION_ID)
            apply()
        }
        flowProvider.reset()
    }

    companion object {
        private const val DEFAULT_SESSION_ID = "no-value"
        private const val DEFAULT_FLOW_ID = "unknown"
        private const val PREF_SESSION_ID = "PREF_SESSION_ID"
    }
}