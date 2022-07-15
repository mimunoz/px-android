package com.mercadopago.android.px.internal.features;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CardTokenException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public interface SecurityCodeActivityView extends MvpView {
    void setSecurityCodeInputMaxLength(int length);

    void showError(MercadoPagoError error, String requestOrigin);

    void setErrorView(CardTokenException exception);

    void clearErrorView();

    void onBackButtonPressed();

    void showLoadingView();

    void stopLoadingView();

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void finishWithResult();

    void initialize();

    void showTimer();

    void showBackSecurityCodeCardView();

    void showFrontSecurityCodeCardView();

    void showUrlSecurityCodeCardView(@Nullable final String securityCodeUrl);

    void showStandardErrorMessage();
}
