package com.mercadopago.android.px.addons.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class SecurityValidationData {

    @NonNull private final String flowId;
    @NonNull private final Map<String, Object> parameters;
    @Nullable private final EscValidationData escValidationData;

    /* default */ SecurityValidationData(@NonNull final Builder builder) {
        flowId = builder.flowId;
        parameters = builder.params;
        escValidationData = builder.escValidationData;
    }

    @NonNull
    public String getFlowId() {
        return flowId;
    }

    @NonNull
    public Map<String, Object> getParams() {
        return parameters;
    }

    @Nullable
    public EscValidationData getEscValidationData() {
        return escValidationData;
    }

    public static final class Builder {
        @NonNull /* default */ String flowId;
        @NonNull /* default */ Map<String, Object> params;
        @Nullable /* default */ EscValidationData escValidationData;

        public Builder(@NonNull final String flowId) {
            this.flowId = flowId;
            params = new HashMap<>();
        }

        /**
         * Data needed to check if should show biometrics challenge in flows that works with esc.
         * @return builder
         */
        public Builder setEscValidationData(@Nullable final EscValidationData escValidationData) {
            this.escValidationData = escValidationData;
            return this;
        }

        /**
         * puts a value in the params map with the given key
         * @return builder
         */
        public Builder putParam(@NonNull final String key, @NonNull final Object value) {
            params.put(key, value);
            return this;
        }

        /**
         * puts a whole params map.
         * @return builder
         */
        public Builder putAllParams(@NonNull final Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public SecurityValidationData build() {
            return new SecurityValidationData(this);
        }
    }
}