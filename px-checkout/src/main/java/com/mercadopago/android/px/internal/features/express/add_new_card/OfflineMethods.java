package com.mercadopago.android.px.internal.features.express.add_new_card;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.viewmodel.AmountLocalized;

public interface OfflineMethods {

    interface OffMethodsView extends MvpView, PayButton.Handler {

        void updateTotalView(@NonNull final AmountLocalized amountLocalized);

        void onSlideSheet(final float offset);

        void startKnowYourCustomerFlow(@NonNull final String flowLink);
    }

    interface Actions {

        void updateModel();

        void selectMethod(@NonNull final OfflineMethodItem selectedItem);

        void onBack();
    }
}