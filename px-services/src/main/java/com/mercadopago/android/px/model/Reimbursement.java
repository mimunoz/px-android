package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.internal.Text;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Reimbursement implements Parcelable, Serializable {

    private final List<Integer> appliedInstallments;
    private final Text installmentRow;
    private final Text card;

    public static final Parcelable.Creator<Reimbursement> CREATOR = new Parcelable.Creator<Reimbursement>() {
        @Override
        public Reimbursement createFromParcel(final Parcel in) {
            return new Reimbursement(in);
        }

        @Override
        public Reimbursement[] newArray(final int size) {
            return new Reimbursement[size];
        }
    };

    protected Reimbursement(final Parcel in) {
        appliedInstallments = new ArrayList<>();
        in.readList(appliedInstallments, null);
        installmentRow = in.readParcelable(Text.class.getClassLoader());
        card = in.readParcelable(Text.class.getClassLoader());
    }

    public boolean hasAppliedInstallment(final int installment) {
        for (final int appliedInstallment : appliedInstallments) {
            if (appliedInstallment == installment) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    public Text getInstallmentRow() {
        return installmentRow;
    }

    public Text getCard() {
        return card;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeList(appliedInstallments);
        dest.writeParcelable(installmentRow, flags);
        dest.writeParcelable(card, flags);
    }
}