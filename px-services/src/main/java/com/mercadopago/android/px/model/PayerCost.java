package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.mercadopago.android.px.internal.util.RateParser;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class PayerCost implements Parcelable, Serializable {

    public static final int NO_SELECTED = -1;

    @NonNull private Integer installments;
    private static final String CFT = "CFT";
    private static final String TEA = "TEA";
    private List<String> labels;
    private String recommendedMessage;
    private BigDecimal installmentRate;
    private BigDecimal totalAmount;
    private BigDecimal installmentAmount;

    // params to support hybrid mode.
    private ProcessingMode processingMode;
    private List<Agreement> agreements;

    @Nullable private String paymentMethodOptionId;

    @NonNull
    public Integer getInstallments() {
        return installments;
    }

    public BigDecimal getInstallmentRate() {
        return installmentRate;
    }

    public List<String> getLabels() {
        return labels;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getTEAPercent() {
        return getRates().get(TEA);
    }

    public String getCFTPercent() {
        return getRates().get(CFT);
    }

    public Map<String, String> getRates() {
        return RateParser.INSTANCE.getRates(labels);
    }

    public boolean hasMultipleInstallments() {
        return installments != null && installments > 1;
    }

    @NonNull
    public ProcessingMode getProcessingMode() {
        return processingMode == null ? ProcessingMode.AGGREGATOR : processingMode;
    }

    @NonNull
    public List<Agreement> getAgreements() {
        return agreements == null ? Collections.emptyList() : agreements;
    }

    @Nullable
    public String getPaymentMethodOptionId() {
        return paymentMethodOptionId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayerCost)) {
            return false;
        }

        final PayerCost payerCost = (PayerCost) o;

        if (!installments.equals(payerCost.installments)) {
            return false;
        }
        if (!totalAmount.equals(payerCost.totalAmount)) {
            return false;
        }
        return installmentAmount.equals(payerCost.installmentAmount);
    }

    @Override
    public int hashCode() {
        int result = installments.hashCode();
        result = 31 * result + totalAmount.hashCode();
        result = 31 * result + installmentAmount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return installments.toString();
    }

    public static PayerCost getPayerCost(@NonNull final List<PayerCost> payerCosts,
        final int userSelectedPayerCost,
        final int defaultSelected) {
        if (userSelectedPayerCost == NO_SELECTED) {
            return payerCosts.get(defaultSelected);
        } else {
            return payerCosts.get(userSelectedPayerCost);
        }
    }

    @Deprecated
    @VisibleForTesting
    public PayerCost() {
    }

    @Deprecated
    public void setLabels(final List<String> labels) {
        this.labels = labels;
    }

    @Deprecated
    public void setInstallments(@NonNull final Integer installments) {
        this.installments = installments;
    }

    @Deprecated
    public void setInstallmentRate(final BigDecimal installmentRate) {
        this.installmentRate = installmentRate;
    }

    @Deprecated
    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Deprecated
    public Boolean hasRates() {
        return hasTEA() && hasCFT();
    }

    @Deprecated
    public Boolean hasCFT() {
        return getCFTPercent() != null;
    }

    @Deprecated
    public Boolean hasTEA() {
        return getTEAPercent() != null;
    }

    public static final Creator<PayerCost> CREATOR = new Creator<PayerCost>() {
        @Override
        public PayerCost createFromParcel(final Parcel in) {
            return new PayerCost(in);
        }

        @Override
        public PayerCost[] newArray(final int size) {
            return new PayerCost[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(installments);

        dest.writeStringList(labels);

        dest.writeString(recommendedMessage);

        if (installmentRate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(installmentRate.toString());
        }

        if (totalAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(totalAmount.toString());
        }

        if (installmentAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(installmentAmount.toString());
        }

        dest.writeParcelable(processingMode, flags);
        dest.writeTypedList(agreements);
        dest.writeString(paymentMethodOptionId);
    }

    protected PayerCost(final Parcel in) {
        installments = in.readInt();
        labels = in.createStringArrayList();
        recommendedMessage = in.readString();

        if (in.readByte() == 0) {
            installmentRate = null;
        } else {
            installmentRate = new BigDecimal(in.readString());
        }

        if (in.readByte() == 0) {
            totalAmount = null;
        } else {
            totalAmount = new BigDecimal(in.readString());
        }

        if (in.readByte() == 0) {
            installmentAmount = null;
        } else {
            installmentAmount = new BigDecimal(in.readString());
        }

        processingMode = in.readParcelable(ProcessingMode.class.getClassLoader());
        agreements = in.createTypedArrayList(Agreement.CREATOR);
        paymentMethodOptionId = in.readString();
    }
}