package com.mercadopago.android.px.internal.util.textformatter;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import androidx.annotation.NonNull;

public final class SpanAdjuster extends MetricAffectingSpan {

    private static final double RATE = 0.5;

    @Override
    public void updateDrawState(@NonNull final TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * RATE);
    }

    @Override
    public void updateMeasureState(@NonNull final TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * RATE);
    }
}