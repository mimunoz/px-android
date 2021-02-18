package com.mercadopago.android.px.internal.datasource;

import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.DiscountParamsConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.mappers.ExpressMetadataToDisabledIdMapper;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.ExpressMetadataRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.ModalRepository;
import com.mercadopago.android.px.internal.repository.PayerComplianceRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.tracking.TrackingRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.ExpressMetadata;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.CheckoutFeatures;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.model.internal.InitRequest;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import java.util.ArrayList;
import java.util.Map;

public class InitService implements InitRepository {

    private static final int MAX_REFRESH_RETRIES = 4;
    private static final int RETRY_DELAY = 500;

    @NonNull private final ESCManagerBehaviour escManagerBehaviour;
    @NonNull private final CheckoutService checkoutService;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final ExperimentsRepository experimentsRepository;
    @NonNull /* default */ DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @NonNull private final MPTracker tracker;
    @NonNull private final PayerPaymentMethodRepository payerPaymentMethodRepository;
    @NonNull private final ExpressMetadataRepository expressMetadataRepository;
    @NonNull private final PaymentMethodRepository paymentMethodRepository;
    @NonNull private final ModalRepository modalRepository;
    @NonNull private PayerComplianceRepository payerComplianceRepository;
    @NonNull private AmountConfigurationRepository amountConfigurationRepository;
    @NonNull private DiscountRepository discountRepository;
    @NonNull private final TrackingRepository trackingRepository;
    /* default */ int refreshRetriesAvailable = MAX_REFRESH_RETRIES;
    /* default */ Handler retryHandler;

    public InitService(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final ExperimentsRepository experimentsRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour, @NonNull final CheckoutService checkoutService,
        @NonNull final TrackingRepository trackingRepository, @NonNull final MPTracker tracker,
        @NonNull final PayerPaymentMethodRepository payerPaymentMethodRepository,
        @NonNull final ExpressMetadataRepository expressMetadataRepository,
        @NonNull final PaymentMethodRepository paymentMethodRepository,
        @NonNull final ModalRepository modalRepository,
        @NonNull final PayerComplianceRepository payerComplianceRepository,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final DiscountRepository discountRepository) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.experimentsRepository = experimentsRepository;
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        this.checkoutService = checkoutService;
        this.trackingRepository = trackingRepository;
        this.tracker = tracker;
        this.payerPaymentMethodRepository = payerPaymentMethodRepository;
        this.expressMetadataRepository = expressMetadataRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.modalRepository = modalRepository;
        this.payerComplianceRepository = payerComplianceRepository;
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.discountRepository = discountRepository;
    }

    @NonNull
    @Override
    public MPCall<InitResponse> init() {
        return newCall(getPostResponse());
    }

    protected void configure(@NonNull final InitResponse initResponse) {
        if (initResponse.getCheckoutPreference() != null) {
            paymentSettingRepository.configure(initResponse.getCheckoutPreference());
        }
        paymentSettingRepository.configure(initResponse.getSite());
        paymentSettingRepository.configure(initResponse.getCurrency());
        paymentSettingRepository.configure(initResponse.getConfiguration());
        experimentsRepository.configure(initResponse.getExperiments());
        payerPaymentMethodRepository.configure(initResponse.getCustomSearchItems());
        expressMetadataRepository.configure(initResponse.getExpress());
        paymentMethodRepository.configure(initResponse.getPaymentMethods());
        modalRepository.configure(initResponse.getModals());
        payerComplianceRepository.configure(initResponse.getPayerCompliance());
        amountConfigurationRepository.configure(initResponse.getDefaultAmountConfiguration());
        discountRepository.configure(initResponse.getDiscountsConfigurations());

        disabledPaymentMethodRepository.storeDisabledPaymentMethodsIds(
            new ExpressMetadataToDisabledIdMapper().map(initResponse.getExpress()));

        tracker.setExperiments(experimentsRepository.getExperiments());
    }

    @Override
    public void lazyConfigure(@NonNull final InitResponse initResponse) {
        getPostResponse().call(initResponse);
    }

    interface PostResponse {
        void call(InitResponse initResponse);
    }

    /* default */ PostResponse getPostResponse() {
        return this::configure;
    }

    private PostResponse noPostResponse() {
        return initResponse -> {
        };
    }

    @NonNull
    private MPCall<InitResponse> newCall(@NonNull final PostResponse postResponse) {
        return new MPCall<InitResponse>() {

            @Override
            public void enqueue(final Callback<InitResponse> callback) {
                newRequest().enqueue(getInternalCallback(callback));
            }

            @NonNull /* default */ Callback<InitResponse> getInternalCallback(
                final Callback<InitResponse> callback) {
                return new Callback<InitResponse>() {
                    @Override
                    public void success(final InitResponse initResponse) {
                        postResponse.call(initResponse);
                        callback.success(initResponse);
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        callback.failure(apiException);
                    }
                };
            }
        };
    }

    @NonNull /* default */ MPCall<InitResponse> newRequest() {
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final PaymentConfiguration paymentConfiguration = paymentSettingRepository.getPaymentConfiguration();

        final AdvancedConfiguration advancedConfiguration = paymentSettingRepository.getAdvancedConfiguration();
        final DiscountParamsConfiguration discountParamsConfiguration =
            advancedConfiguration.getDiscountParamsConfiguration();

        final CheckoutFeatures features = new CheckoutFeatures.Builder()
            .setSplit(paymentConfiguration.getPaymentProcessor().supportsSplitPayment(checkoutPreference))
            .setExpress(advancedConfiguration.isExpressPaymentEnabled())
            .setOdrFlag(true)
            .build();

        final Map<String, Object> body = JsonUtil.getMapFromObject(
            new InitRequest.Builder(paymentSettingRepository.getPublicKey())
                .setCardWithEsc(new ArrayList<>(escManagerBehaviour.getESCCardIds()))
                .setCharges(paymentConfiguration.getCharges())
                .setDiscountParamsConfiguration(discountParamsConfiguration)
                .setCheckoutFeatures(features)
                .setCheckoutPreference(checkoutPreference)
                .setFlow(trackingRepository.getFlowId())
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
    public MPCall<InitResponse> refreshWithNewCard(@NonNull final String cardId) {
        return callback -> newCall(noPostResponse()).enqueue(getRefreshWithNewCardCallback(cardId, callback));
    }

    /* default */ Callback<InitResponse> getRefreshWithNewCardCallback(@NonNull final String cardId,
        @NonNull final Callback<InitResponse> callback) {
        final Map<String, DisabledPaymentMethod> disabledPaymentMethodMap =
            disabledPaymentMethodRepository.getDisabledPaymentMethods();
        return new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                refreshRetriesAvailable--;
                for (final ExpressMetadata node : initResponse.getExpress()) {
                    if (node.isCard() && node.getCard().getId().equals(cardId)) {
                        refreshRetriesAvailable = MAX_REFRESH_RETRIES;
                        new ExpressMetadataSorter(initResponse.getExpress(), disabledPaymentMethodMap)
                            .setPrioritizedCardId(cardId).sort();
                        getPostResponse().call(initResponse);
                        callback.success(initResponse);
                        return;
                    }
                }
                if (refreshRetriesAvailable > 0) {
                    if (retryHandler == null) {
                        final HandlerThread thread = new HandlerThread("MyInitRetryThread");
                        thread.start();
                        retryHandler = new Handler(thread.getLooper());
                    }
                    retryHandler.postDelayed(() -> refreshWithNewCard(cardId).enqueue(callback), RETRY_DELAY);
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
