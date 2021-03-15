package com.mercadopago.android.px.addons;

import android.content.Context;
import androidx.annotation.NonNull;

@Deprecated
public interface LocaleBehaviour {

    /**
     * @param context new context to attach
     * @return new context wrapped with custom locale
     */
    @NonNull
    Context attachBaseContext(@NonNull final Context context);
}