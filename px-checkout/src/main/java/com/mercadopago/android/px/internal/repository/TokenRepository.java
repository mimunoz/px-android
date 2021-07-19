package com.mercadopago.android.px.internal.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.model.RemotePaymentToken;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Token;

public interface TokenRepository {

    MPCall<Token> createToken(@NonNull final Card card, @Nullable final RemotePaymentToken remotePaymentToken);
    MPCall<Token> createTokenWithoutCvv(@NonNull final Card card, @Nullable final RemotePaymentToken remotePaymentToken);
}
