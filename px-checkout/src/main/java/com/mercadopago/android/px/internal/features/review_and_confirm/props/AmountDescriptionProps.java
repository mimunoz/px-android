package com.mercadopago.android.px.internal.features.review_and_confirm.props;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;

public class AmountDescriptionProps {

    public final BigDecimal amount;
    public final String description;
    public final Currency currency;
    public final Integer textColor;
    public final String descriptionType;

    public AmountDescriptionProps(@NonNull final BigDecimal amount,
        @NonNull final String description,
        @NonNull final Currency currency,
        @NonNull final Integer textColor,
        @Nullable final String descriptionType) {

        this.amount = amount;
        this.description = description;
        this.currency = currency;
        this.textColor = textColor;
        this.descriptionType = descriptionType;
    }
}