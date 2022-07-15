package com.mercadopago.android.px.internal.features.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Token;

public class ReviewAndConfirmViewModel implements Parcelable {

    public final String paymentMethodId;
    public final String lastFourDigits;
    public final Integer accreditationTime;
    public final String issuerName;
    public final boolean moreThanOnePaymentMethod;
    public final String paymentMethodName;
    private final String paymentType;
    public final long issuerId;

    public ReviewAndConfirmViewModel(final PaymentMethod paymentMethod,
        @NonNull final String lastFourDigits,
        final Issuer issuer,
        final boolean moreThanOnePaymentMethod) {

        paymentMethodId = paymentMethod.getId();
        paymentMethodName = paymentMethod.getName();
        paymentType = paymentMethod.getPaymentTypeId();
        accreditationTime = paymentMethod.getAccreditationTime();
        //Token and issuer are not always available
        this.lastFourDigits = lastFourDigits;
        issuerName = issuer != null ? issuer.getName() : null;
        issuerId = issuer != null ? issuer.getId() : 0L;
        this.moreThanOnePaymentMethod = moreThanOnePaymentMethod;
    }

    protected ReviewAndConfirmViewModel(final Parcel in) {
        paymentMethodId = in.readString();
        lastFourDigits = in.readString();
        if (in.readByte() == 0) {
            accreditationTime = null;
        } else {
            accreditationTime = in.readInt();
        }
        issuerName = in.readString();
        moreThanOnePaymentMethod = in.readByte() != 0;
        paymentMethodName = in.readString();
        paymentType = in.readString();
        issuerId = in.readLong();
    }

    public static final Creator<ReviewAndConfirmViewModel> CREATOR = new Creator<ReviewAndConfirmViewModel>() {
        @Override
        public ReviewAndConfirmViewModel createFromParcel(final Parcel in) {
            return new ReviewAndConfirmViewModel(in);
        }

        @Override
        public ReviewAndConfirmViewModel[] newArray(final int size) {
            return new ReviewAndConfirmViewModel[size];
        }
    };

    public boolean hasMoreThanOnePaymentMethod() {
        return moreThanOnePaymentMethod;
    }

    public String getPaymentType() {
        return paymentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(paymentMethodId);
        dest.writeString(lastFourDigits);
        if (accreditationTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(accreditationTime);
        }
        dest.writeString(issuerName);
        dest.writeByte((byte) (moreThanOnePaymentMethod ? 1 : 0));
        dest.writeString(paymentMethodName);
        dest.writeString(paymentType);
        dest.writeLong(issuerId);
    }
}