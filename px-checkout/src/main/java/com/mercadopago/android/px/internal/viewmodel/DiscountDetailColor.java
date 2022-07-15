package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.R;

public final class DiscountDetailColor implements IDetailColor {

    @Override
    public int getColor(@NonNull final Context context) {
        return ContextCompat.getColor(context, R.color.px_expressCheckoutTextColorDiscount);
    }
}