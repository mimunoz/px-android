package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GatewayService {

    @POST("/v1/card_tokens")
    MPCall<Token> createToken(@Query("public_key") String publicKey,
        @Body CardToken cardToken);

    @POST("/v1/card_tokens")
    MPCall<Token> createToken(@Query("public_key") String publicKey,
        @Body SavedCardToken savedCardToken);

    @POST("/v1/card_tokens")
    MPCall<Token> createToken(@Query("public_key") String publicKey,
        @Body SavedESCCardToken savedESCCardToken);

    @POST("/v1/card_tokens/{token_id}/clone")
    MPCall<Token> cloneToken(@Path(value = "token_id") String tokenId, @Query("public_key") String publicKey);

    @PUT("/v1/card_tokens/{token_id}")
    MPCall<Token> updateToken(@Path(value = "token_id") String tokenId,
        @Query("public_key") String publicKey,
        @Body SecurityCodeIntent securityCodeIntent);

    @DELETE("{environment}/px_mobile/v1/esc_cap/{card_id}")
    MPCall<String> clearCap(@Path(value = "environment", encoded = true) String environment,
        @Path(value = "card_id") String cardId);
}