package com.mercadopago.android.px.internal.features.business_result;

import androidx.annotation.Nullable;
import com.mercadolibre.android.mlbusinesscomponents.components.discount.MLBusinessDiscountBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.domain.response.MLBusinessTouchpointResponse;
import com.mercadolibre.android.mlbusinesscomponents.components.touchpoint.tracking.MLBusinessTouchpointTracker;

public interface PXDiscountBoxData {

    @Nullable
    String getTitle();

    @Nullable
    String getSubtitle();

    @Nullable
    MLBusinessTouchpointResponse getTouchpoint();

    @Nullable
    MLBusinessTouchpointTracker getTracker();

    MLBusinessDiscountBoxData getDiscountBoxData();
}
