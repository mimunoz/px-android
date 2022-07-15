package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.ExpressMetadata;

public class ExpressMetadataToDisabledIdMapper extends NonNullMapper<ExpressMetadata, String> {
    @Override
    public String map(@NonNull final ExpressMetadata val) {
        return val.getStatus().isEnabled() ? null : val.getCustomOptionId();
    }
}