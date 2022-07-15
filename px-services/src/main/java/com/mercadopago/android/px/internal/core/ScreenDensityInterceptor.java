package com.mercadopago.android.px.internal.core;

import android.content.Context;
import android.util.DisplayMetrics;
import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class ScreenDensityInterceptor implements Interceptor {

    private static final String HEADER_KEY = "X-Density";

    @NonNull private final String density;

    public ScreenDensityInterceptor(@NonNull final Context context) {
        density = getDensityName(context);
    }

    @Override
    public Response intercept(@NonNull final Chain chain) throws IOException {
        final Request originalRequest = chain.request();
        final Request request = originalRequest.newBuilder()
            .header(HEADER_KEY, density)
            .build();
        return chain.proceed(request);
    }

    private String getDensityName(@NonNull final Context context) {
        final float densityScale = 1.0f / DisplayMetrics.DENSITY_DEFAULT;
        final float density = context.getResources().getDisplayMetrics().density / densityScale;

        if (density >= DisplayMetrics.DENSITY_XXXHIGH) {
            return "xxxhdpi";
        }
        if (density >= DisplayMetrics.DENSITY_XXHIGH) {
            return "xxhdpi";
        }
        if (density >= DisplayMetrics.DENSITY_XHIGH) {
            return "xhdpi";
        }
        if (density >= DisplayMetrics.DENSITY_HIGH) {
            return "hdpi";
        }
        if (density >= DisplayMetrics.DENSITY_MEDIUM) {
            return "mdpi";
        }
        return "ldpi";
    }
}