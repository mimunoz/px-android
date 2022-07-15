package com.mercadopago.android.px.internal.features.payer_information;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.repository.IdentificationRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.IdentificationUtils;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.PayerInformationStateModel;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.InvalidFieldException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.views.CPFViewTracker;
import com.mercadopago.android.px.tracking.internal.views.LastNameViewTracker;
import com.mercadopago.android.px.tracking.internal.views.NameViewTracker;
import java.util.List;

/* default */ class PayerInformationPresenter extends BasePresenter<PayerInformation.View>
    implements PayerInformation.Actions {

    @NonNull /* default */ final PayerInformationStateModel state;
    @NonNull
    private final PaymentSettingRepository paymentSettings;

    @NonNull
    private final IdentificationRepository identificationRepository;

    @Nullable
    private final PaymentMethod paymentMethodInformation;

    private FailureRecovery mFailureRecovery;

    private static final int DEFAULT_IDENTIFICATION_NUMBER_LENGTH = 12;

    /* default */ PayerInformationPresenter(@NonNull final PayerInformationStateModel state,
        @NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final IdentificationRepository identificationRepository,
        @Nullable final PaymentMethod paymentMethod) {
        this.state = state;
        this.paymentSettings = paymentSettings;
        this.identificationRepository = identificationRepository;
        paymentMethodInformation = paymentMethod;
    }

    @Override
    public void attachView(final PayerInformation.View view) {
        super.attachView(view);

        if (state.hasIdentificationTypes()) {
            view.hideProgressBar();
        } else {
            view.showProgressBar();
            getIdentificationTypesAsync();
        }

        if (state.hasFilledInfo()) {
            view.initializeIdentificationTypes(state.getIdentificationTypeList(), state.getIdentificationType());
            view.setName(state.getIdentificationName());
            view.setLastName(state.getIdentificationLastName());
            view.setNumber(state.getIdentificationNumber());
            view.identificationDraw();
            restoreViewFocus();
        }
    }

    /* default */ void getIdentificationTypesAsync() {
        identificationRepository.getIdentificationTypes().enqueue(
            new TaggedCallback<List<IdentificationType>>(ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES) {
                @Override
                public void onSuccess(final List<IdentificationType> identificationTypes) {
                    if(isViewAttached()) {
                        resolveIdentificationTypes(identificationTypes);
                        getView().hideProgressBar();
                        getView().showIdentificationNumberFocus();
                    }
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    if (isViewAttached()) {
                        getView().showError(error, ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
                        setFailureRecovery(() -> {
                            getView().showProgressBar();
                            getIdentificationTypesAsync();
                        });
                    }
                }
            });
    }

    /* default */ void resolveIdentificationTypes(final List<IdentificationType> identificationTypes) {
        state.setIdentificationTypes(identificationTypes);
        if (identificationTypes.isEmpty()) {
            getView().showMissingIdentificationTypesError();
        } else {
            getView().initializeIdentificationTypes(identificationTypes, state.getIdentificationType());
        }
    }

    public void saveIdentificationNumber(final String identificationNumber) {
        state.setIdentificationNumber(identificationNumber);
    }

    public void saveIdentificationName(final String identificationName) {
        state.setIdentificationName(identificationName);
    }

    public void saveIdentificationLastName(final String identificationLastName) {
        state.setIdentificationLastName(identificationLastName);
    }

    public void saveIdentificationBusinessName(final String identificationBusinessName) {
        state.saveIdentificationBusinessName(identificationBusinessName);
    }

    public int getIdentificationNumberMaxLength() {
        int maxLength = DEFAULT_IDENTIFICATION_NUMBER_LENGTH;

        if (state.getIdentificationType() != null) {
            maxLength = state.getIdentificationType().getMaxLength();
        }
        return maxLength;
    }

    public void saveIdentificationType(final IdentificationType identificationType) {
        state.setIdentificationType(identificationType);
        if (identificationType != null) {
            state.getIdentification().setType(identificationType.getId());
            getView().setIdentificationNumberRestrictions(identificationType.getType());
        }
    }

    @Override
    public void createPayer() {
        //Get current payer
        final CheckoutPreference checkoutPreference = paymentSettings.getCheckoutPreference();
        final Payer payer = checkoutPreference.getPayer();
        // add collected information.

        //Business name is first name in v1/payments
        if (IdentificationUtils.isCnpj(state.getIdentificationType())) {
            payer.setFirstName(state.getIdentificationBusinessName());
            payer.setLastName(TextUtil.EMPTY);
        } else {
            payer.setFirstName(state.getIdentificationName());
            payer.setLastName(state.getIdentificationLastName());
        }
        payer.setIdentification(state.getIdentification());
        // reconfigure
        paymentSettings.configure(checkoutPreference);
    }

    /* default */ void setFailureRecovery(final FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }

    @VisibleForTesting
    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    private void resolveInvalidFieldException(final InvalidFieldException e) {
        if (e.getErrorCode() == InvalidFieldException.INVALID_IDENTIFICATION_LENGHT) {
            getView().showInvalidLengthIdentificationNumberErrorView();
            getView().showErrorIdentificationNumber();
        } else {
            getView().showInvalidIdentificationNumberErrorView();
            getView().showErrorIdentificationNumber();
        }
    }

    @Override
    public void validateName() {
        if (TextUtil.isNotEmpty(state.getIdentificationName())) {
            getView().clearErrorView();
            getView().clearErrorName();
            getView().showIdentificationLastNameFocus();
        } else {
            getView().showInvalidIdentificationNameErrorView();
            getView().showErrorName();
        }
    }

    @Override
    public void validateLastName() {
        if (TextUtil.isNotEmpty(state.getIdentificationLastName())) {
            getView().clearErrorView();
            getView().clearErrorLastName();
            getView().showCardFlowEnd(state.getCurrentFocusType());
        } else {
            getView().showInvalidIdentificationLastNameErrorView();
            getView().showErrorLastName();
        }
    }

    @Override
    public void validateBusinessName() {
        if (TextUtil.isNotEmpty(state.getIdentificationBusinessName())) {
            getView().clearErrorView();
            getView().clearErrorBusinessName();
            getView().showCardFlowEnd(state.getCurrentFocusType());
        } else {
            getView().showInvalidIdentificationBusinessNameErrorView();
            getView().showErrorBusinessName();
        }
    }

    @Override
    public void validateIdentification() {
        try {
            IdentificationUtils.validateTicketIdentification(state.getIdentification(), state.getIdentificationType());
            getView().clearErrorView();
            getView().clearErrorIdentificationNumber();
            showIdentificationNumberNextScreen();
        } catch (final InvalidFieldException e) {
            resolveInvalidFieldException(e);
        }
    }

    private void showIdentificationNumberNextScreen() {
        if (IdentificationUtils.isCnpj(state.getIdentificationType())) {
            getView().showIdentificationBusinessNameFocus();
        } else {
            getView().showIdentificationNameFocus();
        }
    }

    @Override
    public void trackAbort() {
        tracker.trackAbort();
    }

    @Override
    public void setCurrentFocus(@PayerInformationFocus final String currentFocusType) {
        switch (currentFocusType) {
        case PayerInformationFocus.NUMBER_INPUT:
            final CPFViewTracker cpfViewTracker = new CPFViewTracker(paymentMethodInformation);
            setCurrentViewTracker(cpfViewTracker);
            break;
        case PayerInformationFocus.NAME_INPUT:
            final NameViewTracker nameViewTracker = new NameViewTracker(paymentMethodInformation);
            setCurrentViewTracker(nameViewTracker);
            break;
        case PayerInformationFocus.LAST_NAME_INPUT:
            final LastNameViewTracker lastNameViewTracker = new LastNameViewTracker(paymentMethodInformation);
            setCurrentViewTracker(lastNameViewTracker);
            break;
        }
        state.setFocus(currentFocusType);
    }

    @Override
    public void configureIdentificationTypeFlow(@NonNull final IdentificationType identificationType) {
        if (IdentificationUtils.isCnpj(identificationType)) {
            getView().configureCnpjFlow();
        } else {
            getView().configureCpfFlow();
        }
    }

    @Override
    public void validateCurrentEditText() {
        switch (state.getCurrentFocusType()) {
        case PayerInformationFocus.NUMBER_INPUT:
            validateIdentification();
            break;
        case PayerInformationFocus.NAME_INPUT:
            validateName();
            break;
        case PayerInformationFocus.LAST_NAME_INPUT:
            validateLastName();
            break;
        case PayerInformationFocus.BUSINESS_NAME_INPUT:
            validateBusinessName();
            break;
        }
    }

    @Override
    public void onSaveInstance(@NonNull final Bundle bundle) {
        state.toBundle(bundle);
    }

    private void restoreViewFocus() {
        switch (state.getCurrentFocusType()) {
        case PayerInformationFocus.NAME_INPUT:
            getView().showIdentificationNameFocus();
            break;
        case PayerInformationFocus.LAST_NAME_INPUT:
            getView().showIdentificationLastNameFocus();
            break;
        case PayerInformationFocus.BUSINESS_NAME_INPUT:
            getView().showIdentificationBusinessNameFocus();
            break;
        default:
            getView().showIdentificationNumberFocus();
            break;
        }
    }

    @Override
    public void onBackPressed() {
        tracker.trackBack();
        switch (state.getCurrentFocusType()) {
        case PayerInformationFocus.NAME_INPUT:
        case PayerInformationFocus.BUSINESS_NAME_INPUT:
            getView().showIdentificationNumberFocus();
            break;
        case PayerInformationFocus.LAST_NAME_INPUT:
            getView().showIdentificationNameFocus();
            break;
        case PayerInformationFocus.NUMBER_INPUT:
        default:
            getView().cancel();
            break;
        }
    }
}