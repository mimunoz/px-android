package com.mercadopago.android.px.internal.features.cardvault;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.model.Reason;
import java.util.List;

/* default */  interface CardVault {

    /* default */  interface View extends MvpView {

        void finishWithResult();

        void showApiExceptionError(final ApiException exception, final String requestOrigin);

        void showError(final MercadoPagoError mercadoPagoError, final String requestOrigin);

        void showEmptyPayerCostScreen();

        void startIssuersActivity(@NonNull final List<Issuer> issuers);

        void startSecurityCodeActivity(final Reason reason);

        void showProgressLayout();

        void askForCardInformation();

        void askForSecurityCodeFromTokenRecovery(@NonNull final Reason recoveryReason);

        void cancelCardVault();

        void animateTransitionSlideInSlideOut();

        void finishOnErrorResult();

        void askForInstallments(CardInfo cardInfo);
    }

    /* default */  interface Actions {

        void initialize();

        void setPaymentRecovery(PaymentRecovery paymentRecovery);

        void setCard(@Nullable Card card);

        @Nullable
        Token getToken();

        void setToken(Token mToken);

        @Nullable
        PaymentMethod getPaymentMethod();

        void setPaymentMethod(PaymentMethod mPaymentMethod);

        @Nullable
        PaymentRecovery getPaymentRecovery();

        // TODO: can we kill this and use the selected card on user selection repository?
        @Nullable
        Card getCard();

        void setCardInfo(CardInfo cardInfo);

        @Nullable
        CardInfo getCardInfo();

        int getCardNumberLength();

        void resolveInstallmentsRequest();

        void resolveSecurityCodeRequest();

        void resolveNewCardRequest(final Intent data);

        void onResultCancel();

        void onResultFinishOnError();
    }
}