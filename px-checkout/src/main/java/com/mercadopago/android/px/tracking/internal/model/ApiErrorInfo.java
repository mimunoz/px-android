package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Cause;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

@SuppressWarnings("unused")
@Keep
public class ApiErrorInfo {

    private String apiStatusCode;
    private String apiErrorMessage;
    private String apiUrl;
    private boolean retryAvailable;

    public ApiErrorInfo(@Nullable final String apiStatusCode, @NonNull final String apiErrorMessage,
        @NonNull final String apiUrl, final boolean retryAvailable) {
        this.apiStatusCode = apiStatusCode;
        this.apiErrorMessage = apiErrorMessage;
        this.apiUrl = apiUrl;
        this.retryAvailable = retryAvailable;
    }

    public static ApiErrorInfo createFrom(@NonNull final MercadoPagoError error) {
        final boolean isApiException = error.getApiException() != null;
        String statusCode = null;
        final String errorMessage;
        if (isApiException) {
            final Cause cause = error.getApiException().getFirstCause();
            if (cause == null || TextUtil.isEmpty(cause.getDescription())) {
                errorMessage = error.getMessage();
            } else {
                errorMessage = cause.getDescription();
                statusCode = cause.getCode();
            }
        } else {
            errorMessage = error.getMessage();
        }
        return new ApiErrorInfo(statusCode, errorMessage, error.getRequestOrigin(), error.isRecoverable());
    }
}
