package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceEventHandler;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandler;
import com.mercadopago.android.px.internal.callbacks.PaymentServiceHandlerWrapper;
import com.mercadopago.android.px.internal.model.EscStatus;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.InstructionsRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.TokenRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.TokenErrorWrapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodMapper;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Split;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.model.internal.PaymentConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.model.Reason;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kotlin.Pair;

public class PaymentService implements PaymentRepository {

    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final SplitPaymentProcessor paymentProcessor;
    @NonNull private final Context context;
    @NonNull private final TokenRepository tokenRepository;
    @NonNull private final InitRepository initRepository;
    @NonNull private final EscPaymentManager escPaymentManager;
    @NonNull private final ESCManagerBehaviour escManagerBehaviour;

    @NonNull /* default */ final PaymentServiceHandlerWrapper handlerWrapper;
    @NonNull /* default */ final AmountConfigurationRepository amountConfigurationRepository;
    @NonNull /* default */ final UserSelectionRepository userSelectionRepository;
    @NonNull /* default */ final PluginRepository pluginRepository;

    @Nullable private IPaymentDescriptor payment;

    public PaymentService(@NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final PluginRepository pluginRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final AmountRepository amountRepository,
        @NonNull final SplitPaymentProcessor paymentProcessor,
        @NonNull final Context context,
        @NonNull final EscPaymentManager escPaymentManager,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final TokenRepository tokenRepository,
        @NonNull final InstructionsRepository instructionsRepository,
        @NonNull final InitRepository initRepository,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository,
        @NonNull final CongratsRepository congratsRepository) {
        this.amountConfigurationRepository = amountConfigurationRepository;
        this.escPaymentManager = escPaymentManager;
        this.escManagerBehaviour = escManagerBehaviour;
        this.userSelectionRepository = userSelectionRepository;
        this.pluginRepository = pluginRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
        this.paymentProcessor = paymentProcessor;
        this.context = context;
        this.tokenRepository = tokenRepository;
        this.initRepository = initRepository;

        handlerWrapper =
            new PaymentServiceHandlerWrapper(this, disabledPaymentMethodRepository, escPaymentManager,
                instructionsRepository, congratsRepository, userSelectionRepository);
    }

    @Override
    public PaymentServiceEventHandler getObservableEvents() {
        return handlerWrapper.getObservableEvents();
    }

    @Override
    public void attach(@NonNull final PaymentServiceHandler handler) {
        handlerWrapper.setHandler(handler);
        handlerWrapper.processMessages();
    }

    @Override
    public void detach(@NonNull final PaymentServiceHandler handler) {
        handlerWrapper.detach(handler);
    }

    @Override
    public void storePayment(@NonNull final IPaymentDescriptor iPayment) {
        payment = iPayment;
    }

    @Nullable
    @Override
    public IPaymentDescriptor getPayment() {
        return payment;
    }

    @Override
    public boolean hasPayment() {
        return payment != null;
    }

    @Override
    public boolean hasRecoverablePayment() {
        return hasPayment() && Payment.StatusDetail.isStatusDetailRecoverable(payment.getPaymentStatusDetail());
    }

    @NonNull
    @Override
    public PaymentRecovery createRecoveryForInvalidESC() {
        return createPaymentRecovery(Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC);
    }

    @NonNull
    @Override
    public PaymentRecovery createPaymentRecovery() {
        return createPaymentRecovery(getPayment().getPaymentStatusDetail());
    }

    @NonNull
    private PaymentRecovery createPaymentRecovery(@NonNull final String statusDetail) {
        final Token token = paymentSettingRepository.getToken();
        final Card card = userSelectionRepository.getCard();
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        return new PaymentRecovery(statusDetail, token, card, paymentMethod);
    }

    /**
     * This method presets all user information ahead before the payment is processed.
     */
    @Override
    public void startExpressPayment(@NonNull final PaymentConfiguration configuration) {
        handlerWrapper.createTransactionLiveData();
        initRepository.init().enqueue(new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                final Pair<String, String> pair = new Pair<>(configuration.getPaymentMethodId(),
                    configuration.getPaymentTypeId());
                final PaymentMethod paymentMethod = new PaymentMethodMapper(initResponse).map(pair);
                userSelectionRepository.select(paymentMethod, null);
                if (PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId())) {
                    // cards
                    final Card card = initResponse.getCardById(configuration.getCustomOptionId());
                    if (configuration.getSplitPayment()) {
                        //TODO refactor
                        final String secondaryPaymentMethodId =
                            amountConfigurationRepository
                                .getConfigurationFor(card.getId())
                                .getSplitConfiguration().secondaryPaymentMethod.paymentMethodId;
                        userSelectionRepository
                            .select(card, initResponse.getPaymentMethodById(secondaryPaymentMethodId));
                    } else if (card != null) {
                        userSelectionRepository.select(card, null);
                    }
                }
                userSelectionRepository.select(configuration.getPayerCost());

                startPayment();
            }

            @Override
            public void failure(final ApiException apiException) {
                throw new IllegalStateException("empty payment methods");
            }
        });
    }

    @Override
    public void startPayment() {
        //Wrapping the callback is important to assure ESC handling.
        checkPaymentMethod();
    }

    /* default */ void checkPaymentMethod() {
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        if (paymentMethod != null) {
            processPaymentMethod(paymentMethod);
        } else {
            handlerWrapper.onPaymentError(getError());
        }
    }

    private void processPaymentMethod(final PaymentMethod paymentMethod) {
        if (PaymentTypes.isCardPaymentType(paymentMethod.getPaymentTypeId())) {
            payWithCard();
        } else {
            pay();
        }
    }

    private void payWithCard() {
        if (hasValidSavedCardInfo()) {
            if (paymentSettingRepository.hasToken()) { // Paying with saved card with token
                pay();
            } else { // Token does not exists - must generate one or ask for CVV.
                checkEscAvailability();
            }
        } else if (hasValidNewCardInfo()) { // New card payment
            pay();
        } else { // Guessing card could not tokenize or obtain card information.
            handlerWrapper.onPaymentError(getError());
        }
    }

    private boolean shouldInvalidateEsc(final String escStatus) {
        return EscStatus.REJECTED.equals(escStatus);
    }

    private void checkEscAvailability() {
        //Paying with saved card without token
        final Card card = userSelectionRepository.getCard();

        final boolean shouldInvalidateEsc = shouldInvalidateEsc(card.getEscStatus());
        if (escManagerBehaviour.isESCEnabled() && escPaymentManager.hasEsc(card) && !shouldInvalidateEsc) {
            //Saved card has ESC - Try to tokenize
            tokenRepository.createToken(card).enqueue(new Callback<Token>() {
                @Override
                public void success(final Token token) {
                    checkPaymentMethod();
                }

                @Override
                public void failure(final ApiException apiException) {
                    //Start CVV screen if fail
                    handlerWrapper.onCvvRequired(card, new TokenErrorWrapper(apiException).toReason());
                }
            });
        } else {
            final Reason reason =
                escManagerBehaviour.isESCEnabled() ? (shouldInvalidateEsc ? Reason.ESC_CAP : Reason.SAVED_CARD)
                    : Reason.ESC_DISABLED;
            handlerWrapper.onCvvRequired(card, reason);
        }
    }

    private boolean hasValidSavedCardInfo() {
        return userSelectionRepository.hasCardSelected()
            && userSelectionRepository.getPayerCost() != null;
    }

    private boolean hasValidNewCardInfo() {
        return userSelectionRepository.getPaymentMethod() != null
            && userSelectionRepository.getIssuer() != null
            && userSelectionRepository.getPayerCost() != null
            && paymentSettingRepository.hasToken();
    }

    private void pay() {
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final String securityType = paymentSettingRepository.getSecurityType().getValue();
        if (paymentProcessor.shouldShowFragmentOnPayment(checkoutPreference)) {
            handlerWrapper.onVisualPayment();
        } else {
            final SplitPaymentProcessor.CheckoutData checkoutData =
                new SplitPaymentProcessor.CheckoutData(getPaymentDataList(), checkoutPreference, securityType);
            paymentProcessor.startPayment(context, checkoutData, handlerWrapper);
        }
    }

    @Override
    public boolean isExplodingAnimationCompatible() {
        return !paymentProcessor.shouldShowFragmentOnPayment(paymentSettingRepository.getCheckoutPreference());
    }

    /**
     * Payment data is a dynamic non-mutable object that represents the payment state of the checkout exp.
     *
     * @return payment data at the moment is called.
     */
    @NonNull
    @Override
    public List<PaymentData> getPaymentDataList() {
        final DiscountConfigurationModel discountModel = discountRepository.getCurrentConfiguration();
        final PaymentMethod secondaryPaymentMethod = userSelectionRepository.getSecondaryPaymentMethod();
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        final PayerCost payerCost = userSelectionRepository.getPayerCost();
        final BigDecimal amountToPay = amountRepository.getAmountToPay(paymentMethod.getPaymentTypeId(), payerCost);

        if (secondaryPaymentMethod != null) { // is split payment
            final AmountConfiguration currentConfiguration = amountConfigurationRepository.getCurrentConfiguration();
            final Split splitConfiguration = currentConfiguration.getSplitConfiguration();

            final PaymentData paymentData = new PaymentData.Builder()
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setToken(paymentSettingRepository.getToken())
                .setIssuer(userSelectionRepository.getIssuer())
                .setPayer(paymentSettingRepository.getCheckoutPreference().getPayer())
                .setTransactionAmount(amountToPay)
                .setCampaign(discountModel.getCampaign())
                .setDiscount(splitConfiguration.primaryPaymentMethod.discount)
                .setRawAmount(splitConfiguration.primaryPaymentMethod.amount)
                .createPaymentData();

            final PaymentData secondaryPaymentData = new PaymentData.Builder()
                .setTransactionAmount(amountToPay)
                .setPayer(paymentSettingRepository.getCheckoutPreference().getPayer())
                .setPaymentMethod(secondaryPaymentMethod)
                .setCampaign(discountModel.getCampaign())
                .setDiscount(splitConfiguration.secondaryPaymentMethod.discount)
                .setRawAmount(splitConfiguration.secondaryPaymentMethod.amount)
                .createPaymentData();

            return Arrays.asList(paymentData, secondaryPaymentData);
        } else { // is regular 1 pm payment
            Discount discount;

            try {
                discount = Discount.replaceWith(discountModel.getDiscount(),
                    amountConfigurationRepository.getCurrentConfiguration().getDiscountToken());
            } catch (final IllegalStateException e) {
                discount = discountModel.getDiscount();
            }

            final PaymentData paymentData = new PaymentData.Builder()
                .setPaymentMethod(paymentMethod)
                .setPayerCost(payerCost)
                .setToken(paymentSettingRepository.getToken())
                .setIssuer(userSelectionRepository.getIssuer())
                .setDiscount(discount)
                .setPayer(paymentSettingRepository.getCheckoutPreference().getPayer())
                .setTransactionAmount(amountToPay)
                .setCampaign(discountModel.getCampaign())
                .setRawAmount(amountRepository.getAmountWithoutDiscount(paymentMethod.getPaymentTypeId(), payerCost))
                .createPaymentData();

            return Collections.singletonList(paymentData);
        }
    }

    /**
     * Transforms IPayment into a {@link PaymentResult}
     *
     * @param payment The payment model
     * @return The transformed {@link PaymentResult}
     */
    @NonNull
    @Override
    public PaymentResult createPaymentResult(@NonNull final IPaymentDescriptor payment) {
        return new PaymentResult.Builder()
            .setPaymentData(getPaymentDataList())
            .setPaymentId(payment.getId())
            .setPaymentMethodId(payment.getPaymentMethodId())
            .setPaymentStatus(payment.getPaymentStatus())
            .setStatementDescription(payment.getStatementDescription())
            .setPaymentStatusDetail(payment.getPaymentStatusDetail())
            .build();
    }

    @Override
    public int getPaymentTimeout() {
        return paymentProcessor.getPaymentTimeout(paymentSettingRepository.getCheckoutPreference());
    }

    public MercadoPagoError getError() {
        return new MercadoPagoError("Something went wrong", false);
    }
}