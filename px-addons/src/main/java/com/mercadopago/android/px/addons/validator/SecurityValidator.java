package com.mercadopago.android.px.addons.validator;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.addons.validator.internal.SecurityRules;

public final class SecurityValidator {

    @NonNull private final ESCManagerBehaviour escManagerBehaviour;

    public SecurityValidator(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
    }

    public boolean validate(@NonNull final SecurityValidationData securityValidationData) {
        return new SecurityRules(escManagerBehaviour).apply(securityValidationData);
    }
}