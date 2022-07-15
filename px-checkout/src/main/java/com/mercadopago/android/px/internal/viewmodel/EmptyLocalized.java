package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.TextUtil;

public class EmptyLocalized implements ILocalizedCharSequence {
    @Override
    public CharSequence get(@NonNull final Context context) {
        return TextUtil.EMPTY;
    }
}
