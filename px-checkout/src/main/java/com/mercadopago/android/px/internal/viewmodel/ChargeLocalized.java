package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;

public class ChargeLocalized implements ILocalizedCharSequence {

    @Nullable private final String label;

    public ChargeLocalized(@Nullable final String label) {
        this.label = label;
    }

    @Override
    public CharSequence get(@NonNull final Context context) {
        return TextUtil.isNotEmpty(label) ? label : context.getResources().getString(R.string.px_review_summary_charges);
    }
}