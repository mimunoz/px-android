package com.mercadopago.android.px.addons;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import androidx.annotation.NonNull;
import java.util.Locale;

/**
 * Note that this wrapper can be used only from an activity context. Using it in the application context will throw a
 * ClassCastException. Created by mfeldsztejn on 4/5/17.
 */
public final class LocaleContextWrapper extends ContextWrapper {

    /**
     * Constructor
     *
     * @param base the base context to wrap
     */
    private LocaleContextWrapper(final Context base) {
        super(base);
    }

    /**
     * Static method to wrap the context
     *
     * @param activityContext The activity context to wrap
     * @param newLocale       The locale to set
     * @return A new ContextWrapper with the wrapped Context
     */
    @NonNull
    public static ContextWrapper wrap(@NonNull final Context activityContext, @NonNull final Locale newLocale) {
        return new LocaleContextWrapper(updateBaseContext(activityContext, newLocale));
    }

    /**
     * To avoid inconsistencies between the current context and the application context the locale is forced both
     * places. The entry point may vary depending on the vertical or deeplink that the application launches and this
     * mechanism ensures consistency application wide.
     *
     * @param context   The context to wrap
     * @param newLocale The locale to set
     * @return a context with locale configuration changed.
     */
    @SuppressWarnings("PMD.AvoidReassigningParameters")
    @NonNull
    public static Context updateBaseContext(@NonNull Context context, @NonNull final Locale newLocale) {
        context = applyNewLocaleConfig(context, newLocale);
        applyNewLocaleConfig(context.getApplicationContext(), newLocale);
        return context;
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    @NonNull
    private static Context applyNewLocaleConfig(@NonNull Context context, @NonNull final Locale newLocale) {
        final Configuration newLocaleConfig = context.getResources().getConfiguration();
        final Resources res = context.getResources();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final LocaleList localeList = new LocaleList(newLocale);
            newLocaleConfig.setLocales(localeList);
            newLocaleConfig.setLocale(newLocale);
            context = context.createConfigurationContext(newLocaleConfig);
        } else {
            newLocaleConfig.locale = newLocale;
        }

        Locale.setDefault(newLocale);
        res.updateConfiguration(newLocaleConfig, res.getDisplayMetrics());
        return context;
    }
}