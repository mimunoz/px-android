package com.mercadopago.android.px.addons;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.Locale;

public interface LocaleBehaviour {

    /**
     * @param context new context to attach
     * @return new context wrapped with custom locale
     */
    @NonNull
    Context attachBaseContext(@NonNull final Context context);


    /**
     * @return the locale being used in the app.
     */
    @NonNull
    default Locale getLocale() {
        return Locale.getDefault();
    }
}