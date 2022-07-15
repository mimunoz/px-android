package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.Serializable;

public final class MerchantAccount implements Parcelable, Serializable {

    @NonNull private String id;
    @NonNull private String paymentMethodOptionId;
    @Nullable private String branchId;

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getPaymentMethodOptionId() {
        return paymentMethodOptionId;
    }

    @Nullable
    public String getBranchId() {
        return branchId;
    }

    /* default */ MerchantAccount(final Parcel in) {
        id = in.readString();
        paymentMethodOptionId = in.readString();
        branchId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        dest.writeString(paymentMethodOptionId);
        dest.writeString(branchId);
    }

    public static final Creator<MerchantAccount> CREATOR = new Creator<MerchantAccount>() {
        @Override
        public MerchantAccount createFromParcel(final Parcel in) {
            return new MerchantAccount(in);
        }

        @Override
        public MerchantAccount[] newArray(final int size) {
            return new MerchantAccount[size];
        }
    };
}
