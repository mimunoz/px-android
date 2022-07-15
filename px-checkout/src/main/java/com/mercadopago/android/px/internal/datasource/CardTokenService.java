package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import com.mercadopago.android.px.services.Callback;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT_NEW;

public class CardTokenService implements CardTokenRepository {

    /* default */ @NonNull final PaymentSettingRepository paymentSettingRepository;
    /* default */ @NonNull final ESCManagerBehaviour escManagerBehaviour;
    @NonNull private final Device device;
    @NonNull private final GatewayService gatewayService;

    public CardTokenService(@NonNull final GatewayService gatewayService,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final Device device,
        @NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.gatewayService = gatewayService;
        this.paymentSettingRepository = paymentSettingRepository;
        this.device = device;
        this.escManagerBehaviour = escManagerBehaviour;
    }

    /* default */ Callback<Token> wrap(@NonNull final CardToken card, final Callback<Token> callback) {
        final CardInfo cardInfo = CardInfo.create(card);
        return new Callback<Token>() {
            @Override
            public void success(final Token token) {
                escManagerBehaviour
                    .saveESCWith(cardInfo.getFirstSixDigits(), cardInfo.getLastFourDigits(), token.getEsc());
                paymentSettingRepository.configure(token);
                callback.success(token);
            }

            @Override
            public void failure(final ApiException apiException) {
                paymentSettingRepository.configure((Token) null);
                callback.failure(apiException);
            }
        };
    }

    @Override
    public MPCall<Token> createTokenAsync(final CardToken cardToken) {
        cardToken.setDevice(device);
        cardToken.setRequireEsc(escManagerBehaviour.isESCEnabled());
        return callback -> gatewayService
            .createToken(paymentSettingRepository.getPublicKey(), paymentSettingRepository.getPrivateKey(),
                cardToken).enqueue(wrap(cardToken, callback));
    }

    @Override
    public MPCall<Token> createToken(final SavedCardToken savedCardToken) {
        savedCardToken.setDevice(device);
        return gatewayService
            .createToken(paymentSettingRepository.getPublicKey(), paymentSettingRepository.getPrivateKey(),
                savedCardToken);
    }

    @Override
    public MPCall<Token> createToken(final SavedESCCardToken savedESCCardToken) {
        savedESCCardToken.setDevice(device);
        return gatewayService
            .createToken(paymentSettingRepository.getPublicKey(), paymentSettingRepository.getPrivateKey(),
                savedESCCardToken);
    }

    @Override
    public MPCall<Token> cloneToken(final String tokenId) {
        return gatewayService
            .cloneToken(tokenId, paymentSettingRepository.getPublicKey(), paymentSettingRepository.getPrivateKey());
    }

    @Override
    public MPCall<Token> putSecurityCode(final String securityCode, final String tokenId) {
        final SecurityCodeIntent securityCodeIntent = new SecurityCodeIntent();
        securityCodeIntent.setSecurityCode(securityCode);
        return gatewayService
            .updateToken(tokenId, paymentSettingRepository.getPublicKey(), paymentSettingRepository.getPrivateKey(),
                securityCodeIntent);
    }

    @Override
    public void clearCap(@NonNull final String cardId, @NonNull final ClearCapCallback callback) {
        if (TextUtil.isEmpty(paymentSettingRepository.getPrivateKey())) {
            callback.execute();
            return;
        }
        gatewayService.clearCap(API_ENVIRONMENT_NEW, cardId, paymentSettingRepository.getPrivateKey())
            .enqueue(new Callback<String>() {
                @Override
                public void success(final String s) {
                    callback.execute();
                }

                @Override
                public void failure(final ApiException apiException) {
                    callback.execute();
                }
            });
    }
}