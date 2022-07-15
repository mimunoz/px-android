package com.mercadopago.android.px.internal.features.express.add_new_card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.util.CardFormWithFragmentWrapper;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;

interface /* default */ AddNewCard {

    interface View extends MvpView {
        void startCardForm(@NonNull final CardFormWithFragmentWrapper cardFormWithFragmentWrapper);

        void showPaymentMethods(@Nullable final PaymentMethodSearchItem paymentMethodSearchItem);
    }

    interface Actions {
        void onAddNewCardSelected();
    }
}