package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import com.mercadopago.android.px.services.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;

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
        final RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{\n" +
            "   \"cardholder\": {\n" +
            "       \"name\": \"JohnDoe Anytown\",\n" +
            "       \"identification\": {\n" +
            "           \"number\": \"339.592.238-38\",\n" +
            "           \"type\": \"CPF\"\n" +
            "       }\n" +
            "   },\n" +
            "   \"card_number\": \"2303770003400004\",\n" +
            "   \"security_code\": \"832\",\n" +
            "   \"expiration_year\": 2026,\n" +
            "   \"expiration_month\": 5\n" +
            "}");
        return gatewayService.createToken("APP_USR-5a2b9e27-690d-4f2b-a1a4-8d08b37ee5f3", body);
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