package com.mercadopago.android.px.model.internal;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.IParcelablePaymentDescriptor;
import com.mercadopago.android.px.model.IPayment;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.IPaymentDescriptorHandler;

/**
 * Parcelable version for IPayment description.
 */
public final class GenericPaymentDescriptor implements IParcelablePaymentDescriptor {

    private final String paymentTypeId;
    private final String paymentMethodId;
    @Nullable private final Long id;
    @Nullable private final String statementDescription;
    private final String paymentStatus;
    private final String paymentStatusDetail;

    private GenericPaymentDescriptor(@NonNull final String paymentStatus,
        @NonNull final String paymentStatusDetail,
        @NonNull final String paymentTypeId,
        @NonNull final String paymentMethodId,
        @Nullable final Long id,
        @Nullable final String statementDescription) {
        this.paymentTypeId = paymentTypeId;
        this.paymentMethodId = paymentMethodId;
        this.id = id;
        this.statementDescription = statementDescription;
        this.paymentStatus = paymentStatus;
        this.paymentStatusDetail = paymentStatusDetail;
    }

    public static GenericPaymentDescriptor with(@NonNull final IPayment iPayment) {
        return new GenericPaymentDescriptor(
            iPayment.getPaymentStatus(),
            iPayment.getPaymentStatusDetail(),
            null, null,
            iPayment.getId(),
            iPayment.getStatementDescription()
        );
    }

    public static GenericPaymentDescriptor with(@NonNull final IPaymentDescriptor iPaymentDescriptor) {
        return new GenericPaymentDescriptor(
            iPaymentDescriptor.getPaymentStatus(),
            iPaymentDescriptor.getPaymentStatusDetail(),
            iPaymentDescriptor.getPaymentTypeId(),
            iPaymentDescriptor.getPaymentMethodId(),
            iPaymentDescriptor.getId(),
            iPaymentDescriptor.getStatementDescription()
        );
    }

    private GenericPaymentDescriptor(final Parcel in) {
        paymentTypeId = in.readString();
        paymentMethodId = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        statementDescription = in.readString();
        paymentStatus = in.readString();
        paymentStatusDetail = in.readString();
    }

    public static final Creator<GenericPaymentDescriptor> CREATOR = new Creator<GenericPaymentDescriptor>() {
        @Override
        public GenericPaymentDescriptor createFromParcel(final Parcel in) {
            return new GenericPaymentDescriptor(in);
        }

        @Override
        public GenericPaymentDescriptor[] newArray(final int size) {
            return new GenericPaymentDescriptor[size];
        }
    };

    @NonNull
    @Override
    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    @NonNull
    @Override
    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    @Override
    public void process(@NonNull final IPaymentDescriptorHandler handler) {
        handler.visit(this);
    }

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @Nullable
    @Override
    public String getStatementDescription() {
        return statementDescription;
    }

    @NonNull
    @Override
    public String getPaymentStatus() {
        return paymentStatus;
    }

    @NonNull
    @Override
    public String getPaymentStatusDetail() {
        return paymentStatusDetail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(paymentTypeId);
        dest.writeString(paymentMethodId);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(statementDescription);
        dest.writeString(paymentStatus);
        dest.writeString(paymentStatusDetail);
    }
}
