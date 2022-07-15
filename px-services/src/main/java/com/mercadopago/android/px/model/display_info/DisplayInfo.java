package com.mercadopago.android.px.model.display_info;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.CvvInfo;
import com.mercadopago.android.px.model.internal.Text;
import java.io.Serializable;

public final class DisplayInfo implements Parcelable, Serializable {

    private final LinkableText termsAndConditions;
    private final ResultInfo resultInfo;
    private final Text description;
    @Nullable private CvvInfo cvvInfo;


    public LinkableText getTermsAndConditions() {
        return termsAndConditions;
    }

    public ResultInfo getResultInfo() {
        return resultInfo;
    }

    public Text getDescription() {
        return description;
    }

    public CvvInfo getCvvInfo() {
        return cvvInfo;
    }

    @SuppressWarnings("WeakerAccess")
    protected DisplayInfo(final Parcel in) {
        termsAndConditions = in.readParcelable(LinkableText.class.getClassLoader());
        resultInfo = in.readParcelable(ResultInfo.class.getClassLoader());
        description = in.readParcelable(Text.class.getClassLoader());
        cvvInfo = in.readParcelable(CvvInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(termsAndConditions, flags);
        dest.writeParcelable(resultInfo, flags);
        dest.writeParcelable(description, flags);
        dest.writeParcelable(cvvInfo, flags);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DisplayInfo> CREATOR = new Creator<DisplayInfo>() {
        @Override
        public DisplayInfo createFromParcel(final Parcel in) {
            return new DisplayInfo(in);
        }

        @Override
        public DisplayInfo[] newArray(final int size) {
            return new DisplayInfo[size];
        }
    };
}
