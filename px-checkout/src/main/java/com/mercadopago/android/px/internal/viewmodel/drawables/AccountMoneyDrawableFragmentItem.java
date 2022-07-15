package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AccountMoneyDrawableFragmentItem extends DrawableFragmentItem {

    public static final Creator<AccountMoneyDrawableFragmentItem> CREATOR =
        new Creator<AccountMoneyDrawableFragmentItem>() {
            @Override
            public AccountMoneyDrawableFragmentItem createFromParcel(final Parcel in) {
                return new AccountMoneyDrawableFragmentItem(in);
            }

            @Override
            public AccountMoneyDrawableFragmentItem[] newArray(final int size) {
                return new AccountMoneyDrawableFragmentItem[size];
            }
        };

    public AccountMoneyDrawableFragmentItem(@NonNull final Parameters parameters) {
        super(parameters);
    }

    protected AccountMoneyDrawableFragmentItem(final Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public Fragment draw(@NonNull final PaymentMethodFragmentDrawer drawer) {
        return drawer.draw(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}