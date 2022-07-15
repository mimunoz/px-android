package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import android.widget.ImageView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public interface PaymentMethodViewModel {

    String getPaymentMethodId();

    String getDescription();

    String getDiscountInfo();

    String getComment();

    @DrawableRes
    int getIconResourceId(@NonNull final Context context);

    @DrawableRes
    int getBadgeResourceId(@NonNull final Context context);

    boolean isDisabled();

    void handleOnClick();

    void tint(@NonNull final ImageView icon);
}