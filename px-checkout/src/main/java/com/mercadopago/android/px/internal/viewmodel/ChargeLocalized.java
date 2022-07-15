package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.internal.SummaryInfo;

public class ChargeLocalized implements ILocalizedCharSequence {

    private final SummaryInfo summaryInfo;

    public ChargeLocalized(@NonNull final SummaryInfo summaryInfo) {
        this.summaryInfo = summaryInfo;
    }

    @Override
    public CharSequence get(@NonNull final Context context) {
        return TextUtil.isNotEmpty(summaryInfo.getCharges()) ? summaryInfo.getCharges() :
            context.getResources().getString(R.string.px_review_summary_charges);
    }
}