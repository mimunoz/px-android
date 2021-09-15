package com.mercadopago.android.px.internal.services

import com.mercadopago.android.px.model.internal.CongratsResponse
import com.mercadopago.android.px.model.internal.remedies.RemediesBody
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse
import com.mercadopago.android.px.services.BuildConfig
import retrofit2.http.*

interface CongratsService {

    @GET("${BuildConfig.API_ENVIRONMENT}/px_mobile/congrats")
    suspend fun getCongrats(
        @Header("X-Location-Enabled") locationEnabled: Boolean,
        @Query("public_key") publicKey: String,
        @Query("payment_ids") paymentIds: String,
        @Query("platform") platform: String,
        @Query("campaign_id") campaignId: String,
        @Query("ifpe") turnedIFPECompliant: Boolean,
        @Query("payment_methods_ids") paymentMethodsIds: String,
        @Query("payment_type_id") paymentTypeId: String,
        @Query("flow_name") flowName: String,
        @Query("merchant_order_id") merchantOrderId: Long?,
        @Query("pref_id") preferenceId: String?): CongratsResponse

    //@POST("${BuildConfig.API_ENVIRONMENT_NEW}/px_mobile/v1/remedies/{payment_id}")
    @POST("https://run.mocky.io/v3/64c7262d-e114-41ae-a687-44a885c72ce2")
    suspend fun getRemedies(
        //@Path(value = "payment_id", encoded = true) paymentId: String,
        @Query(value = "payment_id", encoded = true) paymentId: String,
        @Query("one_tap") oneTap: Boolean,
        @Body body: RemediesBody): RemediesResponse
}
