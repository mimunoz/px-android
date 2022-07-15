package com.mercadopago.android.px.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;

//TODO move to internal package.
public class PaymentRecovery implements Serializable {

    private final String statusDetail;
    private final Token token;
    private final Card card;
    private final PaymentMethod paymentMethod;

    @Deprecated
    public PaymentRecovery(final String paymentStatusDetail) {
        this(paymentStatusDetail, null, null);
    }

    @Deprecated
    public PaymentRecovery(@NonNull final String statusDetail, @NonNull final Token token) {
        this(statusDetail, token, null);
    }

    @Deprecated
    public PaymentRecovery(@NonNull final String statusDetail, @NonNull final Token token, @Nullable final Card card) {
        this(statusDetail, token, card, null);
    }

    public PaymentRecovery(@NonNull final String statusDetail, @NonNull final Token token, @Nullable final Card card,
        @NonNull final PaymentMethod paymentMethod) {
        this.statusDetail = statusDetail;
        this.token = token;
        this.card = card;
        this.paymentMethod = paymentMethod;
    }

    public Token getToken() {
        return token;
    }

    @Nullable
    public Card getCard() {
        return card;
    }

    @NonNull
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isTokenRecoverable() {
        return Payment.StatusDetail.isStatusDetailRecoverable(statusDetail);
    }

    public boolean isStatusDetailCallForAuthorize() {
        return Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE.equals(statusDetail);
    }

    public boolean isStatusDetailCardDisabled() {
        return Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED.equals(statusDetail);
    }

    public boolean isStatusDetailInvalidESC() {
        return Payment.StatusDetail.STATUS_DETAIL_INVALID_ESC.equals(statusDetail);
    }

    public boolean shouldAskForCvv() {
        return !Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED.equals(statusDetail);
    }
}