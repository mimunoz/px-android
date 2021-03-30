package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.TokenRepository;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.util.TokenErrorWrapper;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInformation;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.events.EscFrictionEventTracker;
import com.mercadopago.android.px.tracking.internal.events.TokenFrictionEventTracker;
import java.util.Objects;

public class TokenizeService implements TokenRepository {

    @NonNull private final GatewayService gatewayService;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final ESCManagerBehaviour escManagerBehaviour;
    @NonNull private final Device device;
    @NonNull /* default */ final MPTracker tracker;

    public TokenizeService(@NonNull final GatewayService gatewayService,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final Device device,
        @NonNull final MPTracker tracker) {
        this.gatewayService = gatewayService;
        this.paymentSettingRepository = paymentSettingRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        this.device = device;
        this.tracker = tracker;
    }

    @Override
    public MPCall<Token> createToken(@NonNull final Card card) {
        return callback -> {
            final String cardId = Objects.requireNonNull(card.getId());
            final String esc = escManagerBehaviour.getESC(cardId, card.getFirstSixDigits(), card.getLastFourDigits());

            gatewayService.createToken(
                paymentSettingRepository.getPublicKey(),
                paymentSettingRepository.getPrivateKey(),
                SavedESCCardToken.createWithEsc(cardId, esc, device)).enqueue(wrap(card, esc, callback));
        };
    }

    @Override
    public MPCall<Token> createTokenWithoutCvv(@NonNull final Card card) {
        return callback -> {
            final SavedCardToken savedCardToken = new SavedCardToken(Objects.requireNonNull(card.getId()));
            savedCardToken.setDevice(device);

            gatewayService.createToken(
                paymentSettingRepository.getPublicKey(),
                paymentSettingRepository.getPrivateKey(),
                savedCardToken).enqueue(wrap(card, callback));
        };
    }

    /* default */ Callback<Token> wrap(@NonNull final Card card, final String esc,
        final Callback<Token> callback) {

        final String cardId = Objects.requireNonNull(card.getId());

        return new Callback<Token>() {
            @Override
            public void success(final Token token) {
                //TODO move to esc manager  / Token repo
                escManagerBehaviour.saveESCWith(cardId, token.getEsc());
                token.setLastFourDigits(Objects.requireNonNull(card.getLastFourDigits()));
                paymentSettingRepository.configure(token);
                callback.success(token);
            }

            @Override
            public void failure(final ApiException apiException) {
                //TODO move to esc manager / Token repo
                final TokenErrorWrapper tokenError = new TokenErrorWrapper(apiException);
                paymentSettingRepository.configure((Token) null);
                escManagerBehaviour.deleteESCWith(cardId, tokenError.toEscDeleteReason(), tokenError.getValue());
                if (tokenError.isKnownTokenError()) {
                    // Just limit the tracking to esc api exception
                    tracker.track(EscFrictionEventTracker.create(cardId, esc, apiException));
                } else {
                    tracker.track(TokenFrictionEventTracker.create(tokenError.getValue()));
                }

                callback.failure(apiException);
            }
        };
    }

    /* default */ Callback<Token> wrap(@NonNull final CardInformation card, final Callback<Token> callback) {
        return new Callback<Token>() {
            @Override
            public void success(final Token token) {
                token.setLastFourDigits(Objects.requireNonNull(card.getLastFourDigits()));
                paymentSettingRepository.configure(token);
                callback.success(token);
            }

            @Override
            public void failure(final ApiException apiException) {
                final TokenErrorWrapper tokenError = new TokenErrorWrapper(apiException);
                paymentSettingRepository.configure((Token) null);
                tracker.track(TokenFrictionEventTracker.create(tokenError.getValue()));
                callback.failure(apiException);
            }
        };
    }
}
