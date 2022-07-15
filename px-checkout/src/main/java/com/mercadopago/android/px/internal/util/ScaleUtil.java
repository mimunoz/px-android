package com.mercadopago.android.px.internal.util;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;

public final class ScaleUtil {

    private ScaleUtil() {
    }

    public static int getPxFromDp(final int dpValue, final Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isLowRes(final Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final boolean dpiLowRes = metrics.densityDpi < DisplayMetrics.DENSITY_HIGH;
        final boolean heightLowRes = metrics.heightPixels < 800;
        return dpiLowRes || heightLowRes;
    }
}