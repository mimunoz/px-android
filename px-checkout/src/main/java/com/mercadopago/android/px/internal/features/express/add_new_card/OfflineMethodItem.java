package com.mercadopago.android.px.internal.features.express.add_new_card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.internal.Text;

/* default */ final class OfflineMethodItem {

    @NonNull private final Text name;
    @Nullable private String paymentMethodId;
    @Nullable private String paymentTypeId;
    @Nullable private Text description;
    private int iconId;
    private boolean additionalInfoNeeded;

    public OfflineMethodItem(@NonNull final Text name) {
        this.name = name;
    }

    public OfflineMethodItem(@NonNull final String paymentMethodId, @NonNull final String paymentTypeId,
        @NonNull final Text name, @NonNull final Text description, final int iconId,
        final boolean additionalInfoNeeded) {
        this.paymentMethodId = paymentMethodId;
        this.paymentTypeId = paymentTypeId;
        this.name = name;
        this.description = description;
        this.iconId = iconId;
        this.additionalInfoNeeded = additionalInfoNeeded;
    }

    @Nullable
    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    @Nullable
    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    @NonNull
    public Text getName() {
        return name;
    }

    @Nullable
    public Text getDescription() {
        return description;
    }

    public int getIconId() {
        return iconId;
    }

    public boolean isAdditionalInfoNeeded() {
        return additionalInfoNeeded;
    }

    public boolean isOfflinePaymentTypeItem() {
        return description == null && iconId == 0;
    }
}
