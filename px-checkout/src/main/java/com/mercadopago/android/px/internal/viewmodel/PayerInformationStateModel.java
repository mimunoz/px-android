package com.mercadopago.android.px.internal.viewmodel;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.features.payer_information.PayerInformationFocus;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Identification;
import com.mercadopago.android.px.model.IdentificationType;
import java.util.List;

public final class PayerInformationStateModel implements Parcelable {

    private static final String BUNDLE_PAYER_INFO = "bundle_payer_info";

    private String identificationNumber;
    private String identificationName;
    private String identificationLastName;
    private String identificationBusinessName;
    private final Identification identification;
    private IdentificationType identificationType;
    private List<IdentificationType> identificationTypeList;
    @PayerInformationFocus private String currentFocusType;

    private PayerInformationStateModel() {
        identification = new Identification();
    }

    protected PayerInformationStateModel(final Parcel in) {
        identificationNumber = in.readString();
        identificationName = in.readString();
        identificationLastName = in.readString();
        identificationBusinessName = in.readString();
        currentFocusType = in.readString();
        identification = in.readParcelable(Identification.class.getClassLoader());
        identificationType = in.readParcelable(IdentificationType.class.getClassLoader());
        identificationTypeList = in.createTypedArrayList(IdentificationType.CREATOR);
    }

    public static final Creator<PayerInformationStateModel> CREATOR = new Creator<PayerInformationStateModel>() {
        @Override
        public PayerInformationStateModel createFromParcel(final Parcel in) {
            return new PayerInformationStateModel(in);
        }

        @Override
        public PayerInformationStateModel[] newArray(final int size) {
            return new PayerInformationStateModel[size];
        }
    };

    public void toBundle(@NonNull final Bundle bundle) {
        bundle.putParcelable(BUNDLE_PAYER_INFO, this);
    }

    public static PayerInformationStateModel fromBundle(@Nullable final Bundle bundle) {
        if (bundle == null) {
            return new PayerInformationStateModel();
        }
        return bundle.getParcelable(BUNDLE_PAYER_INFO);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(identificationNumber);
        dest.writeString(identificationName);
        dest.writeString(identificationLastName);
        dest.writeString(identificationBusinessName);
        dest.writeString(currentFocusType);
        dest.writeParcelable(identification, flags);
        dest.writeParcelable(identificationType, flags);
        dest.writeTypedList(identificationTypeList);
    }

    public boolean hasIdentificationTypes() {
        return identificationTypeList != null && !identificationTypeList.isEmpty();
    }

    public void setIdentificationTypes(final List<IdentificationType> identificationTypes) {
        identificationTypeList = identificationTypes;
        if (!identificationTypeList.isEmpty()) {
            setIdentificationType(identificationTypeList.get(0));
        }
    }

    public boolean hasFilledInfo() {
        return TextUtil.isNotEmpty(identificationNumber) ||
            TextUtil.isNotEmpty(identificationLastName) ||
            TextUtil.isNotEmpty(identificationName) ||
            TextUtil.isNotEmpty(identificationBusinessName) ||
            identificationType != null;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public String getIdentificationName() {
        return identificationName;
    }

    public String getIdentificationLastName() {
        return identificationLastName;
    }

    public String getIdentificationBusinessName() {
        return identificationBusinessName;
    }

    public Identification getIdentification() {
        return identification;
    }

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public List<IdentificationType> getIdentificationTypeList() {
        return identificationTypeList;
    }

    public void setIdentificationNumber(final String identificationNumber) {
        this.identificationNumber = identificationNumber;
        identification.setNumber(identificationNumber);
    }

    public void setIdentificationName(final String identificationName) {
        this.identificationName = identificationName;
    }

    @PayerInformationFocus
    public String getCurrentFocusType() {
        return currentFocusType;
    }

    public void setIdentificationType(final IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public void setIdentificationLastName(final String identificationLastName) {
        this.identificationLastName = identificationLastName;
    }

    public void saveIdentificationBusinessName(final String identificationBusinessName) {
        this.identificationBusinessName = identificationBusinessName;
    }

    public void setFocus(@PayerInformationFocus final String currentFocusType) {
        this.currentFocusType = currentFocusType;
    }
}