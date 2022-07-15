package com.mercadopago.android.px.internal.features.review_and_confirm;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;

public interface ReviewAndConfirm {

    interface View extends MvpView {

        void showDynamicDialog(@NonNull final DynamicDialogCreator creator,
            @NonNull final DynamicDialogCreator.CheckoutData checkoutData);

        void finishAndChangePaymentMethod();
    }

    interface Presenter {
        void onPrePayment(@NonNull final PayButton.OnReadyForPaymentCallback callback);

        void onChangePaymentMethod();

        void onBackPressed();

        void onPostPaymentAction(@NonNull final PostPaymentAction postPaymentAction);
    }
}