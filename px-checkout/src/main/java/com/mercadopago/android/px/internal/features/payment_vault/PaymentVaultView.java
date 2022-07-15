package com.mercadopago.android.px.internal.features.payment_vault;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.viewmodel.PaymentMethodViewModel;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentVaultView extends MvpView {

    void showSelectedItem(PaymentMethodSearchItem item);

    void showProgress();

    void hideProgress();

    void showSearchItems(List<PaymentMethodViewModel> searchItems);

    void showError(MercadoPagoError mercadoPagoError, String requestOrigin);

    void setTitle(String title);

    void startCardFlow();

    void startPaymentMethodsSelection(final PaymentPreference paymentPreference);

    void finishPaymentMethodSelection(PaymentMethod selectedPaymentMethod);

    void showAmount(@NonNull final DiscountConfigurationModel discountModel,
        @NonNull final BigDecimal totalAmount,
        @NonNull final Currency currency);

    void hideAmountRow();

    void collectPayerInformation();

    void showDetailDialog(@NonNull final Currency currency, @NonNull final DiscountConfigurationModel discountModel);

    void showEmptyPaymentMethodsError();

    void showMismatchingPaymentMethodError();

    void saveAutomaticSelection(final boolean automaticSelection);

    void showDisabledPaymentMethodDetailDialog(@NonNull final DisabledPaymentMethod disabledPaymentMethod);

    void cancel(@Nullable final Intent data);

    void overrideTransitionInOut();

    void showInstallments();
}