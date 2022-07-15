package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;
import java.math.BigDecimal;

public class AccountMoneyMetadata implements Serializable, Parcelable {

    @SerializedName("available_balance")
    public final BigDecimal balance;
    public final boolean invested;
    public final AccountMoneyDisplayInfo displayInfo;

    public static final Creator<AccountMoneyMetadata> CREATOR = new Creator<AccountMoneyMetadata>() {
        @Override
        public AccountMoneyMetadata createFromParcel(final Parcel in) {
            return new AccountMoneyMetadata(in);
        }

        @Override
        public AccountMoneyMetadata[] newArray(final int size) {
            return new AccountMoneyMetadata[size];
        }
    };

    protected AccountMoneyMetadata(final Parcel in) {
        invested = in.readByte() != 0;
        balance = ParcelableUtil.getBigDecimal(in);
        displayInfo = in.readParcelable(AccountMoneyDisplayInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeByte((byte) (invested ? 1 : 0));
        ParcelableUtil.write(dest, balance);
        dest.writeParcelable(displayInfo, flags);
    }

    @NonNull
    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isInvested() {
        return invested;
    }
}