package com.mercadopago.android.px.model.internal;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

public final class Action implements Parcelable {

    public static final Creator<com.mercadopago.android.px.model.internal.Action> CREATOR =
        new Creator<com.mercadopago.android.px.model.internal.Action>() {
            @Override
            public com.mercadopago.android.px.model.internal.Action createFromParcel(final Parcel in) {
                return new com.mercadopago.android.px.model.internal.Action(in);
            }

            @Override
            public com.mercadopago.android.px.model.internal.Action[] newArray(final int size) {
                return new com.mercadopago.android.px.model.internal.Action[size];
            }
        };

    private final String label;
    private final String target;

    @SuppressWarnings("ProtectedMemberInFinalClass")
    protected Action(final Parcel in) {
        label = in.readString();
        target = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(label);
        dest.writeString(target);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getLabel() {
        return label;
    }

    @Nullable
    public String getTarget() {
        return target;
    }
}
