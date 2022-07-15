package com.mercadopago.android.px.addons.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class EscValidationData {

    @Nullable private final String cardId;
    private final boolean isCard;
    private final boolean escEnable;

    /* default */ EscValidationData(@NonNull final Builder builder) {
        cardId = builder.cardId;
        isCard = builder.isCard;
        escEnable = builder.escEnable;
    }

    @Nullable
    public String getCardId() {
        return cardId;
    }

    public boolean isCard() {
        return isCard;
    }

    public boolean isEscEnable() {
        return escEnable;
    }

    public static final class Builder {
        @Nullable /* default */ final String cardId;
        /* default */ final boolean isCard;
        /* default */ final boolean escEnable;

        /**
         * @param cardId to check if has esc, could be null if is a new card
         * @param isCard indicates if current payment method is card or not
         * @param escEnable to indicates if the current flow works with esc
         */
        public Builder(@Nullable final String cardId, final boolean isCard, final boolean escEnable) {
            this.cardId = cardId;
            this.isCard = isCard;
            this.escEnable = escEnable;
        }

        /**
         * @param cardId to check if has esc, could be null if is a new card
         * @param isCard indicates if current payment method is card or not
         */
        public Builder(@Nullable final String cardId, final boolean isCard) {
            this.cardId = cardId;
            this.isCard = isCard;
            escEnable = true;
        }

        public EscValidationData build() {
            return new EscValidationData(this);
        }
    }
}