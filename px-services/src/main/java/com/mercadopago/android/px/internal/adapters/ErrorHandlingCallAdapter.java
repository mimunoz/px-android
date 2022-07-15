package com.mercadopago.android.px.internal.adapters;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.services.Callback;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

public final class ErrorHandlingCallAdapter {

    private ErrorHandlingCallAdapter() {
    }

    public static class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

        @Override
        public CallAdapter<MPCall<?>, MPCallAdapter> get(@NonNull final Type returnType,
            @NonNull final Annotation[] annotations, @NonNull final Retrofit retrofit) {
            final TypeToken<?> token = TypeToken.get(returnType);
            if (!token.getRawType().equals(MPCall.class)) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                    "MPCall must have generic type (e.g., MPCall<ResponseBody>)");
            }
            final Type responseType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            return new CallAdapter<MPCall<?>, MPCallAdapter>() {
                @Override
                public Type responseType() {
                    return responseType;
                }

                @Override
                public MPCallAdapter adapt(@NonNull final Call<MPCall<?>> call) {
                    return new MPCallAdapter<>(call);
                }
            };
        }
    }

    /**
     * Adapts a {@link Call} to {@link MPCall}.
     */
    /* default */ static class MPCallAdapter<T> implements MPCall<T> {

        private static final int SUCCESS_STATUS_CODE = 200;
        private static final int REDIRECT_STATUS_CODE = 300;
        private final Call<T> call;

        /* default */ MPCallAdapter(final Call<T> call) {
            this.call = call;
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            call.enqueue(new retrofit2.Callback<T>() {
                @Override
                public void onResponse(@NonNull final Call<T> call, @NonNull final Response<T> response) {
                    executeOnMainThread(() -> {
                        final int code = response.code();
                        if (code >= SUCCESS_STATUS_CODE && code < REDIRECT_STATUS_CODE) {
                            //Get body
                            final T body = response.body();
                            callback.success(body);
                        } else {
                            callback.failure(ApiUtil.getApiException(response));
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull final Call<T> call, @NonNull final Throwable th) {
                    if (++callback.attempts == 3 || (th instanceof SocketTimeoutException)) {
                        executeOnMainThread(() -> callback.failure(ApiUtil.getApiException(th)));
                    } else {
                        call.clone().enqueue(this);
                    }
                }
            });
        }
    }

    @SuppressWarnings("WeakerAccess")
    /* default */ static void executeOnMainThread(@NonNull final Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }
}