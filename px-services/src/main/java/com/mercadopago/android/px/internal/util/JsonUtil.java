package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public final class JsonUtil {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .registerTypeAdapterFactory(ObjectMapTypeAdapter.FACTORY)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
    }

    private JsonUtil() {
    }

    public static <T> T fromJson(@Nullable final String json, @NonNull final Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(@Nullable final String json, @NonNull final Type classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    private static Map<String, Object> getMapFromJson(@Nullable final String json) {
        return GSON.fromJson(
            json, new TypeToken<ObjectMapTypeAdapter.ObjectMapType>() {
            }.getType()
        );
    }

    public static Map<String, Object> getMapFromObject(@Nullable final Object src) {
        return getMapFromJson(GSON.toJson(src));
    }

    public static String toJson(final Object src) {
        return GSON.toJson(src);
    }

    public static Gson getGson() {
        return GSON;
    }
}