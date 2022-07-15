package com.mercadopago.android.px.addons.internal;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.LocaleBehaviour;

public final class LocaleDefaultBehaviour implements LocaleBehaviour {

    @NonNull
    @Override
    public Context attachBaseContext(@NonNull final Context context) {
        return context;
    }
}
