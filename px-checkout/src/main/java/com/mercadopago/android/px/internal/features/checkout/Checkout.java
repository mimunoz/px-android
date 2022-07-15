package com.mercadopago.android.px.internal.features.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.cardform.internal.LifecycleListener;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

/* default */ interface Checkout {
    /* default */ interface View extends MvpView {

        void showError(final MercadoPagoError error);

        void showProgress();

        void showReviewAndConfirm(final boolean isUniquePaymentMethod);

        void showPaymentMethodSelection();

        void finishWithPaymentResult();

        void finishWithPaymentResult(final Integer customResultCode);

        void finishWithPaymentResult(final Payment payment);

        void finishWithPaymentResult(final Integer customResultCode, final Payment payment);

        void cancelCheckout();

        void cancelCheckout(final MercadoPagoError mercadoPagoError);

        void cancelCheckout(final Integer customResultCode, final Boolean paymentMethodEdited);

        void showPaymentProcessorWithAnimation();

        boolean isActive();

        void showOneTap();

        void hideProgress();

        void exitCheckout(final int resCode);

        void transitionOut();

        void showSavedCardFlow(final Card card);

        void showNewCardFlow();

        void showFailureRecoveryError();
    }

    /* default */ interface Actions {
        void initialize();

        void onErrorCancel(@Nullable final MercadoPagoError mercadoPagoError);

        void onPaymentMethodSelectionError(final MercadoPagoError mercadoPagoError);

        void onPaymentMethodSelectionCancel();

        void onReviewAndConfirmCancel();

        void onReviewAndConfirmError(final MercadoPagoError mercadoPagoError);

        void onPaymentResultResponse();

        void onCardFlowResponse();

        void onTerminalError(@NonNull final MercadoPagoError mercadoPagoError);

        void onCardFlowCancel();

        void onCustomReviewAndConfirmResponse(final Integer customResultCode);

        void recoverFromFailure();

        void setFailureRecovery(final FailureRecovery failureRecovery);

        void onCustomPaymentResultResponse(final Integer customResultCode);

        void cancelCheckout();

        void exitWithCode(final int resCode);

        boolean isUniquePaymentMethod();

        void onChangePaymentMethod();

        void onCardAdded(@NonNull final String cardId, @NonNull final LifecycleListener.Callback callback);
    }
}