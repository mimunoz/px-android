package com.mercadopago.android.px.internal.services

import com.mercadopago.android.px.internal.model.CardHolderAuthenticatorBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface CardHolderAuthenticatorService {

    @POST("https://api.mercadopago.com/cardholder_authenticator/app/trxAuthentication/{card_token_id}")
    suspend fun authenticate(
        @Path(value = "card_token_id", encoded = true) cardTokenId: String,
        @Body body: CardHolderAuthenticatorBody): Any
}