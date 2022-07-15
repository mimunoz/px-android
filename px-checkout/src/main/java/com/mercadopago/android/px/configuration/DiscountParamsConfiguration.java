package com.mercadopago.android.px.configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public final class DiscountParamsConfiguration implements Serializable {

    @NonNull private final Set<String> labels;
    @Nullable private final String productId;

    /* default */ DiscountParamsConfiguration(@NonNull final Builder builder) {
        labels = builder.labels;
        productId = builder.productId;
    }

    /**
     * Get additional data needed to apply a specific discount.
     *
     * @return set of labels needed to apply a specific discount.
     */
    @NonNull
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * @deprecated use productId in Advanced Configuration
     */
    @Deprecated
    @Nullable
    public String getProductId() {
        return productId;
    }

    public static class Builder {

        /* default */ @NonNull Set<String> labels;
        /* default */ @Nullable String productId;

        public Builder() {
            labels = new HashSet<>();
            productId = null;
        }

        /**
         * Set additional data needed to apply a specific discount.
         *
         * @param labels are additional data needed to apply a specific discount.
         * @return builder to keep operating.
         */
        public Builder setLabels(@NonNull final Set<String> labels) {
            this.labels = labels;
            return this;
        }

        /**
         * @deprecated use productId in Advanced Configuration
         */
        @Deprecated
        public Builder setProductId(@NonNull final String productId) {
            this.productId = productId;
            return this;
        }

        public DiscountParamsConfiguration build() {
            return new DiscountParamsConfiguration(this);
        }
    }
}
