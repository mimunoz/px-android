package com.mercadopago.android.px.internal.tracking

interface TrackingRepository {
    val sessionId: String
    val flowId: String
    val flowDetail: Map<String, Any>
    val deviceSecured: Boolean
    val securityEnabled: Boolean

    fun configure(model: Model)
    fun reset()

    data class Model(val sessionId: String, val flowId: String?, val flowDetail: Map<String, Any>?) {
        constructor(sessionId: String) : this(sessionId, null, null)
    }
}