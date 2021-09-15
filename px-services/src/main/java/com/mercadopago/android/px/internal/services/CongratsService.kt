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
        @Query("pref_id") preferenceId: String?
    ): CongratsResponse

    @POST("https://run.mocky.io/v3/64c7262d-e114-41ae-a687-44a885c72ce2") //mini
//    @POST("https://run.mocky.io/v3/43ed9a38-5e16-43e0-b941-b3a668967243") //xsmall
//    @POST("https://run.mocky.io/v3/daa72b6a-ad7f-46b3-bc54-143259045d1b") //small
//    @POST("https://run.mocky.io/v3/209c835a-260f-432b-89d9-30d6d1c2f510") //medium
//    @POST("https://run.mocky.io/v3/6ae85d4f-903e-44be-a6cb-58c2ae13e2d0") //large
    //@POST("${BuildConfig.API_ENVIRONMENT_NEW}/px_mobile/v1/remedies/{payment_id}")
    suspend fun getRemedies(
        @Query(value = "payment_id", encoded = true) paymentId: String,
        @Query("one_tap") oneTap: Boolean,
        @Body body: RemediesBody
    ): RemediesResponse
}
