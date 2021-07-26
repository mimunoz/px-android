package com.mercadopago.android.px.internal.datasource;

import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.DiscountParamsConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.features.FeatureProvider;
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.CheckoutRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.ModalRepository;
import com.mercadopago.android.px.internal.repository.OneTapItemRepository;
import com.mercadopago.android.px.internal.repository.PayerComplianceRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.tracking.TrackingRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.CheckoutResponse;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.model.internal.InitRequest;
import com.mercadopago.android.px.model.internal.OneTapItem;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import java.util.List;
import java.util.Map;

public class CheckoutRepositoryImpl implements CheckoutRepository {

    private static final int MAX_REFRESH_RETRIES = 4;
    private static final int DEFAULT_RETRY_DELAY = 500;
    private static final int LONG_RETRY_DELAY = 5000;

    @NonNull private final CheckoutService checkoutService;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final ExperimentsRepository experimentsRepository;
    @NonNull /* default */ DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @NonNull private final MPTracker tracker;
    @NonNull private final PayerPaymentMethodRepository payerPaymentMethodRepository;
    @NonNull private final OneTapItemRepository oneTapItemRepository;
    @NonNull private final PaymentMethodRepository paymentMethodRepository;
    @NonNull private final ModalRepository modalRepository;
    @NonNull private final PayerComplianceRepository payerComplianceRepository;
    @NonNull private final AmountConfigurationRepository amountConfigurationRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final TrackingRepository trackingRepository;
    @NonNull private final CardStatusRepository cardStatusRepository;
    @NonNull private final FeatureProvider featureProvider;
    /* default */ int refreshRetriesAvailable = MAX_REFRESH_RETRIES;
    /* default */ Handler retryHandler;

    public CheckoutRepositoryImpl(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final ExperimentsRepository experimentsRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final CheckoutService checkoutService,
        @NonNull final TrackingRepository trackingRepository, @NonNull final MPTracker tracker,
        @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
        @NonNull final OneTapItemRepository oneTapItemRepository,
        @NonNull final PaymentMethodRepository paymentMethodRepository,
        @NonNull final ModalRepository modalRepository,
        @NonNull final PayerComplianceRepository payerComplianceRepository,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final CardStatusRepository cardStatusRepository,
        @NonNull final FeatureProvider featureProvider) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.experimentsRepository = experimentsRepository;
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
        this.checkoutService = checkoutService;
        this.trackingRepository = trackingRepository;
        this.tracker = tracker;
        this.payerPaymentMethodRepository = payerPaymentMethodRepository;
        this.oneTapItemRepository = oneTapItemRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.modalRepository = modalRepository;
        this.payerComplianceRepository = payerComplianceRepository;
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.discountRepository = discountRepository;
        this.cardStatusRepository = cardStatusRepository;
        this.featureProvider = featureProvider;
    }

    @NonNull
    @Override
    public MPCall<CheckoutResponse> checkout() {
        return newCall(getPostResponse());
    }

    protected void configure(@NonNull final CheckoutResponse checkoutResponse) {
        if (checkoutResponse.getPreference() != null) {
            paymentSettingRepository.configure(checkoutResponse.getPreference());
        }
        paymentSettingRepository.configure(checkoutResponse.getSite());
        paymentSettingRepository.configure(checkoutResponse.getCurrency());
        paymentSettingRepository.configure(checkoutResponse.getConfiguration());
        experimentsRepository.configure(checkoutResponse.getExperiments());
        payerPaymentMethodRepository.configure(checkoutResponse.getPayerPaymentMethods());
        oneTapItemRepository.configure(checkoutResponse.getOneTapItems());
        paymentMethodRepository.configure(checkoutResponse.getAvailablePaymentMethods());
        modalRepository.configure(checkoutResponse.getModals());
        payerComplianceRepository.configure(checkoutResponse.getPayerCompliance());
        amountConfigurationRepository.configure(checkoutResponse.getDefaultAmountConfiguration());
        discountRepository.configure(checkoutResponse.getDiscountsConfigurations());
        disabledPaymentMethodRepository.configure(
            new OneTapItemToDisabledPaymentMethodMapper().map(checkoutResponse.getOneTapItems()));

        tracker.setExperiments(experimentsRepository.getExperiments());
    }

    @Override
    public void lazyConfigure(@NonNull final CheckoutResponse checkoutResponse) {
        getPostResponse().call(checkoutResponse);
    }

    interface PostResponse {
        void call(CheckoutResponse checkoutResponse);
    }

    /* default */ PostResponse getPostResponse() {
        return this::configure;
    }

    private PostResponse noPostResponse() {
        return initResponse -> {
        };
    }

    @NonNull
    private MPCall<CheckoutResponse> newCall(@NonNull final PostResponse postResponse) {
        return new MPCall<CheckoutResponse>() {

            @Override
            public void enqueue(final Callback<CheckoutResponse> callback) {
                newRequest().enqueue(getInternalCallback(callback));
            }

            @NonNull /* default */ Callback<CheckoutResponse> getInternalCallback(
                final Callback<CheckoutResponse> callback) {
                return new Callback<CheckoutResponse>() {
                    @Override
                    public void success(final CheckoutResponse checkoutResponse) {
                        postResponse.call(checkoutResponse);
                        callback.success(checkoutResponse);
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        callback.failure(apiException);
                    }
                };
            }
        };
    }

    @NonNull /* default */ MPCall<CheckoutResponse> newRequest() {
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final PaymentConfiguration paymentConfiguration = paymentSettingRepository.getPaymentConfiguration();

        final AdvancedConfiguration advancedConfiguration = paymentSettingRepository.getAdvancedConfiguration();
        final DiscountParamsConfiguration discountParamsConfiguration =
            advancedConfiguration.getDiscountParamsConfiguration();

        final Map<String, Object> body = JsonUtil.getMapFromObject(
            new InitRequest.Builder(paymentSettingRepository.getPublicKey())
                .setCharges(paymentConfiguration.getCharges())
                .setDiscountParamsConfiguration(discountParamsConfiguration)
                .setCheckoutFeatures(featureProvider.getAvailableFeatures())
                .setCheckoutPreference(checkoutPreference)
                .setFlow(trackingRepository.getFlowId())
                .setCardsStatus(cardStatusRepository.getCardsStatus())
                .build());

        final String preferenceId = paymentSettingRepository.getCheckoutPreferenceId();
        if (preferenceId != null) {
            return checkoutService.checkout(preferenceId, paymentSettingRepository.getPrivateKey(), body);
        } else {
            return checkoutService.checkout(paymentSettingRepository.getPrivateKey(), body);
        }
    }

    @NonNull
    @Override
    public MPCall<CheckoutResponse> refreshWithNewCard(@NonNull final String cardId) {
        return callback -> newCall(noPostResponse()).enqueue(getRefreshWithNewCardCallback(cardId, callback));
    }

    /* default */ Callback<CheckoutResponse> getRefreshWithNewCardCallback(@NonNull final String cardId,
        @NonNull final Callback<CheckoutResponse> callback) {
        final Map<PayerPaymentMethodKey, DisabledPaymentMethod> disabledPaymentMethodMap =
            disabledPaymentMethodRepository.getValue();
        return new Callback<CheckoutResponse>() {
            @Override
            public void success(final CheckoutResponse checkoutResponse) {
                final boolean retryAvailable = --refreshRetriesAvailable > 0;
                final List<OneTapItem> oneTap = checkoutResponse.getOneTapItems();
                boolean cardFound = false;
                boolean retryNeeded = false;
                for (final OneTapItem node : oneTap) {
                    if (node.isCard() && node.getCard().getId().equals(cardId)) {
                        cardFound = true;
                        retryNeeded = node.getCard().getRetry().isNeeded();
                        break;
                    }
                }
                if (cardFound && (!retryNeeded || !retryAvailable)) {
                    refreshRetriesAvailable = MAX_REFRESH_RETRIES;
                    new OneTapItemSorter(oneTap, disabledPaymentMethodMap)
                        .setPrioritizedCardId(cardId).sort();
                    getPostResponse().call(checkoutResponse);
                    callback.success(checkoutResponse);
                } else if (retryAvailable) {
                    final int retryDelay = retryNeeded ? LONG_RETRY_DELAY : DEFAULT_RETRY_DELAY;
                    if (retryHandler == null) {
                        final HandlerThread thread = new HandlerThread("MyInitRetryThread");
                        thread.start();
                        retryHandler = new Handler(thread.getLooper());
                    }
                    retryHandler.postDelayed(() -> refreshWithNewCard(cardId).enqueue(callback), retryDelay);
                } else {
                    callback.failure(new ApiException());
                }
            }

            @Override
            public void failure(final ApiException apiException) {
                callback.failure(apiException);
            }
        };
    }
}
