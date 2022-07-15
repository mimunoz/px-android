package com.mercadopago.android.px.model.internal;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

/**
 * Checkout features contains feature specific params and metadata about integration.
 */
public final class CheckoutFeatures {

    @SerializedName("one_tap")
    private final boolean express;
    private final boolean split;
    @SerializedName("odr")
    private final boolean odrFlag;

    /* default */ CheckoutFeatures(@NonNull final Builder builder) {
        express = builder.express;
        split = builder.split;
        odrFlag = builder.odrFlag;
    }

    public static final class Builder {

        /* default */ boolean split;
        /* default */ boolean express;
        /* default */ boolean odrFlag;

        public Builder setSplit(final boolean split) {
            this.split = split;
            return this;
        }

        public Builder setExpress(final boolean express) {
            this.express = express;
            return this;
        }

        public Builder setOdrFlag(final boolean odrFlag) {
            this.odrFlag = odrFlag;
            return this;
        }

        public CheckoutFeatures build() {
            return new CheckoutFeatures(this);
        }
    }
}