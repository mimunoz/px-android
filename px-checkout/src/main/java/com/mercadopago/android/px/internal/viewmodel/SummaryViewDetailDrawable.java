package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.R;

public final class SummaryViewDetailDrawable implements IDetailDrawable {

    @Override
    public Drawable getDrawable(@NonNull final Context context) {
        return ContextCompat.getDrawable(context, R.drawable.px_helper);
    }
}