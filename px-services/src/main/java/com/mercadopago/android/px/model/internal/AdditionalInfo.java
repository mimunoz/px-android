package com.mercadopago.android.px.model.internal;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.TextUtil;

public final class AdditionalInfo {

    @Nullable
    public static AdditionalInfo newInstance(@Nullable final String additionalInfo) {
        AdditionalInfo additionalInfoDTO = null;
        if (TextUtil.isNotEmpty(additionalInfo)) {
            try {
                additionalInfoDTO = JsonUtil.fromJson(additionalInfo, AdditionalInfo.class);
            } catch (final Exception e) {
                return null;
            }
        }
        return additionalInfoDTO;
    }

    @SerializedName("px_summary")
    @Nullable private SummaryInfo summaryInfo;

    @Nullable
    public SummaryInfo getSummaryInfo() {
        return summaryInfo;
    }
}
