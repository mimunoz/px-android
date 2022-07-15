package com.mercadopago.android.px.internal.util;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.core.ConnectionHelper;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.ErrorActivity;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;

public final class ErrorUtil {

    public static final int ERROR_REQUEST_CODE = 94;
    private static final String PUBLIC_KEY_EXTRA = "publicKey";

    private ErrorUtil() {
    }

    public static void startErrorActivity(final Activity launcherActivity) {
        final String message = launcherActivity.getResources().getString(R.string.px_standard_error_message);
        final MercadoPagoError mercadoPagoError = new MercadoPagoError(message, false);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(final Activity launcherActivity, final String message,
        final boolean recoverable) {
        final MercadoPagoError mercadoPagoError = new MercadoPagoError(message, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(final Activity launcherActivity, final String message,
        final String errorDetail,
        final boolean recoverable) {
        final MercadoPagoError mercadoPagoError = new MercadoPagoError(message, errorDetail, recoverable);
        startErrorActivity(launcherActivity, mercadoPagoError);
    }

    public static void startErrorActivity(final Activity launcherActivity,
        @Nullable final MercadoPagoError mercadoPagoError) {
        final String publicKey =
            Session.getInstance()
                .getConfigurationModule()
                .getPaymentSettings()
                .getPublicKey();

        final Intent intent = new Intent(launcherActivity, ErrorActivity.class);
        intent.putExtra(EXTRA_ERROR, mercadoPagoError);
        intent.putExtra(PUBLIC_KEY_EXTRA, publicKey);
        launcherActivity.startActivityForResult(intent, ERROR_REQUEST_CODE);
    }

    public static void showApiExceptionError(@NonNull final Activity activity,
        final ApiException apiException,
        final String requestOrigin) {

        final MercadoPagoError mercadoPagoError;
        final String errorMessage;

        if (!ConnectionHelper.getInstance().checkConnection()) {
            errorMessage = activity.getString(R.string.px_no_connection_message);
            mercadoPagoError = new MercadoPagoError(errorMessage, true);
        } else {
            mercadoPagoError = new MercadoPagoError(apiException, requestOrigin);
        }
        ErrorUtil.startErrorActivity(activity, mercadoPagoError);
    }

    public static String getStacktraceMessage(final Exception e) {
        final StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static boolean isErrorResult(final Intent data) {
        return data != null && data.getSerializableExtra(EXTRA_ERROR) != null;
    }
}