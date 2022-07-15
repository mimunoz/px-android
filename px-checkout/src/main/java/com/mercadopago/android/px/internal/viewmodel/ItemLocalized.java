package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.internal.SummaryInfo;

public class ItemLocalized implements ILocalizedCharSequence {

    private final SummaryInfo summaryInfo;

    public ItemLocalized(@NonNull final SummaryInfo summaryInfo) {
        this.summaryInfo = summaryInfo;
    }

    @Override
    public CharSequence get(@NonNull final Context context) {
        return TextUtil.isNotEmpty(summaryInfo.getPurpose()) ? summaryInfo.getPurpose() :
            context.getResources().getString(R.string.px_summary_detail_item_description);
    }
}
