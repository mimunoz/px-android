package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;

public class SoldOutDiscountLocalized implements ILocalizedCharSequence {

    @Override
    public CharSequence get(@NonNull final Context context) {
        return context.getResources().getString(R.string.px_used_up_discount_row);
    }
}
