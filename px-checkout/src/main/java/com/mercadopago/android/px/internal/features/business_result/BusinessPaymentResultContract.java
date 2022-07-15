package com.mercadopago.android.px.internal.features.business_result;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.view.PaymentResultBody;
import com.mercadopago.android.px.model.ExitAction;

/* default */ interface BusinessPaymentResultContract {

    /* default */ interface View extends MvpView {
        void configureViews(@NonNull final BusinessPaymentResultViewModel model,
            @NonNull final PaymentResultBody.Listener listener);

        void processCustomExit();

        void processCustomExit(@NonNull final ExitAction action);

        void setStatusBarColor(@ColorRes int color);

        void launchDeepLink(@NonNull final String deepLink);

        void processCrossSellingBusinessAction(@NonNull final String deepLink);
    }

    /* default */ interface Presenter {
        void onFreshStart();

        void onAbort();
    }
}