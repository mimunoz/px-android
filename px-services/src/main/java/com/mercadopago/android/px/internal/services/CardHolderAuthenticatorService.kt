package com.mercadopago.android.px.internal.services

import com.mercadopago.android.px.internal.model.CardHolderAuthenticatorBody
import com.mercadopago.android.px.services.BuildConfig
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

private const val API_VERSION = "v1"
private const val ENVIRONMENT = BuildConfig.API_ENVIRONMENT_NEW

interface CardHolderAuthenticatorService {

    @POST(ENVIRONMENT + "/px_mobile/" + API_VERSION + "authentication/card_holder")
    suspend fun authenticate(
        @Query("card_token") cardTokenId: String,
        @Query("access_token") accessToken: String,
        @Body body: CardHolderAuthenticatorBody): Any
}