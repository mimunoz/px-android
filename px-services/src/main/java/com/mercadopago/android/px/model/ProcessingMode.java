package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

public enum ProcessingMode implements Parcelable {
    @SerializedName("aggregator") AGGREGATOR,
    @SerializedName("gateway") GATEWAY;

    public String asQueryParamName() {
        return name().toLowerCase();
    }

    @Nullable
    public static String asCommaSeparatedQueryParam(@NonNull final ProcessingMode[] processingModes) {
        final StringBuilder commaSeparatedQueryParam = new StringBuilder();
        for (final ProcessingMode processingMode : processingModes) {
            commaSeparatedQueryParam.append(processingMode.asQueryParamName());
            commaSeparatedQueryParam.append(",");
        }
        if(commaSeparatedQueryParam.length() > 0) {
            return commaSeparatedQueryParam.deleteCharAt(commaSeparatedQueryParam.length() - 1).toString();
        } else {
            return null;
        }
    }

    public static final Creator<ProcessingMode> CREATOR = new Creator<ProcessingMode>() {
        @Override
        public ProcessingMode createFromParcel(final Parcel in) {
            return ProcessingMode.values()[in.readInt()];
        }

        @Override
        public ProcessingMode[] newArray(final int size) {
            return new ProcessingMode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }
}