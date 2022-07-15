package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.internal.Text;
import java.io.Serializable;

public final class BenefitsMetadata implements Parcelable, Serializable {

    @Nullable private final Text installmentsHeader;
    @Nullable private final InterestFree interestFree;
    @Nullable private final Reimbursement reimbursement;

    public static final Creator<BenefitsMetadata> CREATOR = new Creator<BenefitsMetadata>() {
        @Override
        public BenefitsMetadata createFromParcel(final Parcel in) {
            return new BenefitsMetadata(in);
        }

        @Override
        public BenefitsMetadata[] newArray(final int size) {
            return new BenefitsMetadata[size];
        }
    };

    protected BenefitsMetadata(final Parcel in) {
        installmentsHeader = in.readParcelable(Text.class.getClassLoader());
        interestFree = in.readParcelable(InterestFree.class.getClassLoader());
        reimbursement = in.readParcelable(Reimbursement.class.getClassLoader());
    }

    @Nullable
    public Text getInstallmentsHeader() {
        return installmentsHeader;
    }

    @Nullable
    public InterestFree getInterestFree() {
        return interestFree;
    }

    @Nullable
    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(installmentsHeader, flags);
        dest.writeParcelable(interestFree, flags);
        dest.writeParcelable(reimbursement, flags);
    }
}