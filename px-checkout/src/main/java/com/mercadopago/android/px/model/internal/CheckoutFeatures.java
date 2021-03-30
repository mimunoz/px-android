package com.mercadopago.android.px.model.internal;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Checkout features contains feature specific params and metadata about integration.
 */
public final class CheckoutFeatures {

    @SerializedName("one_tap")
    private final boolean express;
    private final boolean split;
    @SerializedName("odr")
    private final boolean odrFlag;
    private final boolean comboCard;
    private final boolean hybridCard;
    private final List<String> validationPrograms;

    /* default */ CheckoutFeatures(@NonNull final Builder builder) {
        express = builder.express;
        split = builder.split;
        odrFlag = builder.odrFlag;
        comboCard = builder.comboCard;
        hybridCard = builder.hybridCard;
        validationPrograms = builder.validationPrograms;
    }

    public static final class Builder {

        /* default */ boolean split;
        /* default */ boolean express;
        /* default */ boolean odrFlag;
        /* default */ boolean comboCard;
        /* default */ boolean hybridCard;
        /* default */ List<String> validationPrograms = new ArrayList<>();

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

        public Builder setComboCard(final boolean comboCard) {
            this.comboCard = comboCard;
            return this;
        }

        public Builder setHybridCard(final boolean hybridCard) {
            this.hybridCard = hybridCard;
            return this;
        }

        public Builder addValidationPrograms(final List<String> validationPrograms) {
            this.validationPrograms.addAll(validationPrograms);
            return this;
        }

        public CheckoutFeatures build() {
            return new CheckoutFeatures(this);
        }
    }
}