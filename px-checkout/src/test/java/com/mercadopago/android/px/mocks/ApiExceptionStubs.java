package com.mercadopago.android.px.mocks;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.utils.ResourcesUtil;

public enum ApiExceptionStubs implements JsonStub<ApiException> {
    PAYMENT_METHOD_NOT_FOUND("ax_payment_method_not_found.json"),
    INVALID_IDENTIFICATION_PAYMENT("invalid_identification_payment.json");

    @NonNull private final String fileName;

    ApiExceptionStubs(@NonNull final String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    @Override
    public ApiException get() {
        return JsonUtil.fromJson(getJson(), ApiException.class);
    }

    @NonNull
    @Override
    public String getJson() {
        return ResourcesUtil.getStringResource(fileName);
    }

    @NonNull
    @Override
    public String getType() {
        return "%API_EXCEPTION%";
    }
}
