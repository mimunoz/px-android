package com.mercadopago.android.px.internal.features.guessing_card;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.IdentificationRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.IssuersRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.SummaryAmountRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.SummaryAmount;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.services.Callback;
import java.util.ArrayList;
import java.util.List;

public class GuessingCardPaymentPresenter extends GuessingCardPresenter {

    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final UserSelectionRepository userSelectionRepository;
    @NonNull private final InitRepository initRepository;
    @NonNull private final IssuersRepository issuersRepository;
    @NonNull private final CardTokenRepository cardTokenRepository;
    @NonNull private final IdentificationRepository identificationRepository;
    @NonNull private final AdvancedConfiguration advancedConfiguration;
    @NonNull private final SummaryAmountRepository summaryAmountRepository;

    protected PaymentRecovery paymentRecovery;

    public GuessingCardPaymentPresenter(@NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final InitRepository initRepository,
        @NonNull final IssuersRepository issuersRepository,
        @NonNull final CardTokenRepository cardTokenRepository,
        @NonNull final IdentificationRepository identificationRepository,
        @NonNull final AdvancedConfiguration advancedConfiguration,
        @NonNull final PaymentRecovery paymentRecovery,
        @NonNull final SummaryAmountRepository summaryAmountRepository) {
        super();
        this.userSelectionRepository = userSelectionRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.initRepository = initRepository;
        this.issuersRepository = issuersRepository;
        this.cardTokenRepository = cardTokenRepository;
        this.identificationRepository = identificationRepository;
        this.advancedConfiguration = advancedConfiguration;
        this.paymentRecovery = paymentRecovery;
        this.summaryAmountRepository = summaryAmountRepository;
    }

    @Override
    public void initialize() {
        getView().onValidStart();
        resolveBankDeals();
        getPaymentMethods();
        if (recoverWithCardHolder()) {
            fillRecoveryFields();
        }
    }

    private void fillRecoveryFields() {
        getView().setCardholderName(paymentRecovery.getToken().getCardHolder().getName());
        getView().setIdentificationNumber(paymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
    }

    @Nullable
    @Override
    public PaymentMethod getPaymentMethod() {
        return userSelectionRepository.getPaymentMethod();
    }

    @Override
    public void setPaymentMethod(@Nullable final PaymentMethod paymentMethod) {
        userSelectionRepository.select(paymentMethod, null);
        if (paymentMethod == null) {
            clearCardSettings();
        }
    }

    @Override
    public void getIdentificationTypesAsync() {
        identificationRepository.getIdentificationTypes().enqueue(
            new TaggedCallback<List<IdentificationType>>(ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES) {
                @Override
                public void onSuccess(final List<IdentificationType> identificationTypes) {
                    resolveIdentificationTypes(identificationTypes);
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    if (isViewAttached()) {
                        getView().showError(error, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
                        setFailureRecovery(() -> getIdentificationTypesAsync());
                    }
                }
            });
    }

    @Override
    public void getPaymentMethods() {
        getView().showProgress();
        initRepository.init().enqueue(new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                if (isViewAttached()) {
                    getView().hideProgress();
                    final PaymentPreference paymentPreference =
                        paymentSettingRepository.getCheckoutPreference().getPaymentPreference();
                    paymentMethodGuessingController = new PaymentMethodGuessingController(
                        paymentPreference.getSupportedPaymentMethods(initResponse.getPaymentMethods()),
                        getPaymentTypeId(),
                        paymentPreference.getExcludedPaymentTypes());
                    startGuessingForm();
                }
            }

            @Override
            public void failure(final ApiException apiException) {
                if (isViewAttached()) {
                    getView().hideProgress();
                    setFailureRecovery(() -> getPaymentMethods());
                }
            }
        });
    }

    @Nullable
    @Override
    public String getPaymentTypeId() {
        return userSelectionRepository.getPaymentType();
    }

    private void resolveBankDeals() {
        if (advancedConfiguration.isBankDealsEnabled()) {
            getView().showBankDeals();
        } else {
            getView().hideBankDeals();
        }
    }

    @Override
    public void onIssuerSelected(final Long issuerId) {
        // Empty body, this behavior only exists on CardStoragePresenter
    }

    @Override
    public void onSaveInstanceState(final Bundle outState, final String cardSideState, final boolean lowResActive) {
        if (getPaymentMethod() != null) {
            super.onSaveInstanceState(outState, cardSideState, lowResActive);
            outState.putParcelableArrayList(PAYMENT_TYPES_LIST_BUNDLE,
                (ArrayList<? extends Parcelable>) getPaymentTypes());
        }
    }

    @Override
    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getParcelable(PAYMENT_METHOD_BUNDLE) != null) {
            setPaymentTypesList(savedInstanceState.getParcelableArrayList(PAYMENT_TYPES_LIST_BUNDLE));
            setPaymentRecovery((PaymentRecovery) savedInstanceState.getSerializable(PAYMENT_RECOVERY_BUNDLE));
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void resolveTokenRequest(final Token token) {
        this.token = token;
        paymentSettingRepository.configure(token);
        getIssuers();
    }

    /* default */ void getIssuers() {
        final PaymentMethod paymentMethod = getPaymentMethod();
        if (paymentMethod != null) {
            issuersRepository.getIssuers(paymentMethod.getId(), bin).enqueue(
                new TaggedCallback<List<Issuer>>(ApiUtil.RequestOrigin.GET_ISSUERS) {
                    @Override
                    public void onSuccess(final List<Issuer> issuers) {
                        onIssuers(issuers);
                    }

                    @Override
                    public void onFailure(final MercadoPagoError error) {
                        setFailureRecovery(() -> getIssuers());
                        if (isViewAttached()) {
                            getView().showError(error, ApiUtil.RequestOrigin.GET_ISSUERS);
                        }
                    }
                });
        }
    }

    /* default */ void getInstallments() {
        // Fetch installments and save a default installment if it exists
        summaryAmountRepository.getSummaryAmount(bin).enqueue(new Callback<SummaryAmount>() {
            @Override
            public void success(final SummaryAmount summaryAmount) {
                final AmountConfiguration amountConfiguration =
                    summaryAmount.getAmountConfiguration(summaryAmount.getDefaultAmountConfiguration());

                if (amountConfiguration != null && amountConfiguration.getPayerCosts().size() == 1) {
                    userSelectionRepository.select(amountConfiguration.getPayerCosts().get(0));
                }

                getView().finishCardFlow();
            }

            @Override
            public void failure(final ApiException apiException) {
                if (isViewAttached()) {
                    final String origin = ApiUtil.RequestOrigin.POST_SUMMARY_AMOUNT;
                    getView().showApiExceptionError(apiException, origin);
                    setFailureRecovery(() -> getInstallments());
                }
            }
        });
    }

    public PaymentRecovery getPaymentRecovery() {
        return paymentRecovery;
    }

    public void setPaymentRecovery(final PaymentRecovery paymentRecovery) {
        this.paymentRecovery = paymentRecovery;
        if (recoverWithCardHolder()) {
            saveCardholderName(paymentRecovery.getToken().getCardHolder().getName());
            saveIdentificationNumber(paymentRecovery.getToken().getCardHolder().getIdentification().getNumber());
        }
    }

    private boolean recoverWithCardHolder() {
        return paymentRecovery != null && paymentRecovery.getToken() != null &&
            paymentRecovery.getToken().getCardHolder() != null;
    }

    @Override
    public void createToken() {
        cardTokenRepository
            .createTokenAsync(cardToken).enqueue(new TaggedCallback<Token>(ApiUtil.RequestOrigin.CREATE_TOKEN) {
            @Override
            public void onSuccess(final Token token) {
                resolveTokenRequest(token);
            }

            @Override
            public void onFailure(final MercadoPagoError error) {
                resolveTokenCreationError(error, ApiUtil.RequestOrigin.CREATE_TOKEN);
            }
        });
    }

    /* default */ void onIssuers(final List<Issuer> issuers) {
        if (issuers.size() == 1) {
            userSelectionRepository.select(issuers.get(0));
            if (paymentSettingRepository.getCheckoutPreference().getDefaultInstallments() != null) {
                //We can auto select the installments for sure
                getInstallments();
            } else if (isViewAttached()) {
                getView().finishCardFlow();
            }
        } else if (isViewAttached()) {
            getView().finishCardFlow(issuers);
        }
    }
}