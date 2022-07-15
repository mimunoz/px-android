package com.mercadopago.android.px.internal.features.payment_methods;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.repository.PaymentMethodsRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.util.ArrayList;
import java.util.List;

/* default */ class PaymentMethodsPresenter extends BasePresenter<PaymentMethods.View>
    implements PaymentMethods.Actions {

    private boolean showBankDeals;
    private PaymentPreference paymentPreference;
    private List<String> supportedPaymentTypes;
    private FailureRecovery failureRecovery;
    @NonNull private final UserSelectionRepository userSelectionRepository;
    @NonNull private final PaymentMethodsRepository paymentMethodsRepository;

    public PaymentMethodsPresenter(@NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentMethodsRepository paymentMethodsRepository) {
        this.userSelectionRepository = userSelectionRepository;
        this.paymentMethodsRepository = paymentMethodsRepository;
    }

    @Override
    public void setShowBankDeals(final boolean showBankDeals) {
        this.showBankDeals = showBankDeals;
    }

    @Override
    public void setPaymentPreference(final PaymentPreference paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    @Override
    public void setSupportedPaymentTypes(final List<String> supportedPaymentTypes) {
        this.supportedPaymentTypes = supportedPaymentTypes;
    }

    @Override
    public void start() {
        definePaymentMethodsExclusions();
        retrievePaymentMethods();
        if (showBankDeals && isViewAttached()) {
            getView().showBankDeals();
        }
    }

    private void retrievePaymentMethods() {
        getView().showProgress();
        paymentMethodsRepository
            .getPaymentMethods()
            .enqueue(new TaggedCallback<List<PaymentMethod>>(ApiUtil.RequestOrigin.GET_PAYMENT_METHODS) {
                @Override
                public void onSuccess(final List<PaymentMethod> paymentMethods) {
                    if (isViewAttached()) {
                        getView().showPaymentMethods(getSupportedPaymentMethods(paymentMethods));
                        getView().hideProgress();
                    }
                }

                @Override
                public void onFailure(final MercadoPagoError exception) {
                    if (isViewAttached()) {
                        setFailureRecovery(() -> retrievePaymentMethods());
                        getView().showError(exception);
                        getView().hideProgress();
                    }
                }
            });
    }

    private void definePaymentMethodsExclusions() {
        // Give priority to PaymentPreference over supported payment types
        if (!isPaymentPreferenceSet() && supportedPaymentTypesSet()) {
            final List<String> excludedPaymentTypes = new ArrayList<>();
            for (final String type : PaymentTypes.getAllPaymentTypes()) {
                if (!supportedPaymentTypes.contains(type)) {
                    excludedPaymentTypes.add(type);
                }
            }
            paymentPreference = new PaymentPreference();
            paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        }
    }

    private boolean supportedPaymentTypesSet() {
        return supportedPaymentTypes != null;
    }

    private boolean isPaymentPreferenceSet() {
        return paymentPreference != null;
    }

    private List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> supportedPaymentMethods;
        if (paymentPreference == null) {
            supportedPaymentMethods = paymentMethods;
        } else {
            supportedPaymentMethods = paymentPreference.getSupportedPaymentMethods(paymentMethods);
            supportedPaymentMethods =
                getPaymentMethodsOfType(userSelectionRepository.getPaymentType(), supportedPaymentMethods);
        }
        return supportedPaymentMethods;
    }

    private List<PaymentMethod> getPaymentMethodsOfType(final String paymentTypeId,
        final List<PaymentMethod> paymentMethodList) {

        List<PaymentMethod> validPaymentMethods = new ArrayList<>();
        if (paymentMethodList != null && paymentTypeId != null && !paymentTypeId.isEmpty()) {
            for (final PaymentMethod currentPaymentMethod : paymentMethodList) {
                if (currentPaymentMethod.getPaymentTypeId().equals(paymentTypeId)) {
                    validPaymentMethods.add(currentPaymentMethod);
                }
            }
        } else {
            validPaymentMethods = paymentMethodList;
        }
        return validPaymentMethods;
    }

    private void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    @Override
    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }
}
