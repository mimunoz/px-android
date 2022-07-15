package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.internal.AdditionalInfo;
import com.mercadopago.android.px.model.internal.SummaryInfo;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public class SummaryInfoMapper extends Mapper<CheckoutPreference, SummaryInfo> {

    @Override
    public SummaryInfo map(@NonNull final CheckoutPreference preference) {
        final AdditionalInfo additionalInfo = AdditionalInfo.newInstance(preference.getAdditionalInfo());

        SummaryInfo summaryInfo = additionalInfo != null ? additionalInfo.getSummaryInfo() : null;

        if (summaryInfo == null) {
            final Item firstItem = preference.getItems().get(0);
            final String title =
                TextUtil.isEmpty(firstItem.getDescription()) ? firstItem.getTitle() : firstItem.getDescription();
            summaryInfo = new SummaryInfo(title, firstItem.getPictureUrl());
        }

        return summaryInfo;
    }
}