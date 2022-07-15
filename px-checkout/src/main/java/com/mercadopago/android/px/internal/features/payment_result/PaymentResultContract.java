package com.mercadopago.android.px.internal.features.payment_result;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.exceptions.ApiException;

public interface PaymentResultContract {

    interface View extends MvpView {

        void configureViews(@NonNull final PaymentResultViewModel model, @NonNull final PaymentModel paymentModel,
            @NonNull final PaymentResultBody.Listener listener);

        void showApiExceptionError(@NonNull final ApiException exception, @NonNull final String requestOrigin);

        void showInstructionsError();

        void openLink(@NonNull final String url);

        void finishWithResult(final int resultCode);

        void changePaymentMethod();

        void recoverPayment();

        void copyToClipboard(@NonNull final String content);

        void setStatusBarColor(@ColorRes final int color);

        void launchDeepLink(@NonNull final String deepLink);

        void processCrossSellingBusinessAction(@NonNull final String deepLink);
    }

    interface Presenter {

        void onFreshStart();

        void onAbort();
    }
}