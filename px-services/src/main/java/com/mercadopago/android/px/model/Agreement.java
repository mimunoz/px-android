package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.List;

public final class Agreement implements Parcelable, Serializable {

    @NonNull private List<MerchantAccount> merchantAccounts;

    @NonNull
    public List<MerchantAccount> getMerchantAccounts() {
        return merchantAccounts;
    }

    /* default */ Agreement(final Parcel in) {
        merchantAccounts = in.createTypedArrayList(MerchantAccount.CREATOR);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeTypedList(merchantAccounts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Agreement> CREATOR = new Creator<Agreement>() {
        @Override
        public Agreement createFromParcel(final Parcel in) {
            return new Agreement(in);
        }

        @Override
        public Agreement[] newArray(final int size) {
            return new Agreement[size];
        }
    };
}
