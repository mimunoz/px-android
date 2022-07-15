package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;

public class DisableConfiguration implements Parcelable {

    private final int backgroundColor;
    private final int fontColor;

    public static final Creator<DisableConfiguration> CREATOR = new Creator<DisableConfiguration>() {
        @Override
        public DisableConfiguration createFromParcel(final Parcel in) {
            return new DisableConfiguration(in);
        }

        @Override
        public DisableConfiguration[] newArray(final int size) {
            return new DisableConfiguration[size];
        }
    };

    public DisableConfiguration(@NonNull final Context context) {
        backgroundColor = context.getResources().getColor(R.color.px_disabled_background);
        fontColor = context.getResources().getColor(R.color.px_disabled_font);
    }

    protected DisableConfiguration(final Parcel in) {
        backgroundColor = in.readInt();
        fontColor = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(backgroundColor);
        dest.writeInt(fontColor);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}