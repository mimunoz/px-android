package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model that represents a discount campaign.
 */
@SuppressWarnings("unused")
public class Campaign implements Serializable, Parcelable {

    private final String id;
    private final String codeType;
    private final BigDecimal maxCouponAmount;
    @SerializedName("max_redeem_per_user")
    private final int maxRedeemPerUser;
    @SerializedName("end_date")
    private final Date endDate;
    @SerializedName("legal_terms")
    private final String legalTermsUrl;

    private static final String CODE_TYPE_SINGLE = "single";
    private static final String CODE_TYPE_MULTIPLE = "multiple";
    private static final String CODE_TYPE_NONE = "none";

    /* default */ Campaign(final Builder builder) {
        id = builder.id;
        maxCouponAmount = builder.maxCouponAmount;
        codeType = builder.codeType;
        maxRedeemPerUser = builder.maxRedeemPerUser;
        endDate = builder.endDate;
        legalTermsUrl = builder.legalTermsUrl;
    }

    public int getMaxRedeemPerUser() {
        return maxRedeemPerUser;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getMaxCouponAmount() {
        return maxCouponAmount;
    }

    public String getCodeType() {
        return codeType;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getPrettyEndDate() {
        return DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
            .format(endDate);
    }

    public boolean hasMaxCouponAmount() {
        return maxCouponAmount != null && BigDecimal.ZERO.compareTo(maxCouponAmount) < 0;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public boolean isSingleCodeDiscountCampaign() {
        return CODE_TYPE_SINGLE.contains(codeType);
    }

    public boolean isMultipleCodeDiscountCampaign() {
        return CODE_TYPE_MULTIPLE.contains(codeType);
    }

    public boolean isDirectDiscountCampaign() {
        return CODE_TYPE_NONE.contains(codeType);
    }

    public boolean isAlwaysOnDiscount() {
        return maxRedeemPerUser > 1;
    }

    /* default */ Campaign(final Parcel in) {
        id = in.readString();
        maxCouponAmount = ParcelableUtil.getOptionalBigDecimal(in);
        codeType = in.readString();
        maxRedeemPerUser = in.readInt();
        endDate = new Date(in.readLong());
        legalTermsUrl = in.readString();
    }

    public static final Creator<Campaign> CREATOR = new Creator<Campaign>() {
        @Override
        public Campaign createFromParcel(final Parcel in) {
            return new Campaign(in);
        }

        @Override
        public Campaign[] newArray(final int size) {
            return new Campaign[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        ParcelableUtil.writeOptional(dest, maxCouponAmount);
        dest.writeString(codeType);
        dest.writeInt(maxRedeemPerUser);
        dest.writeLong(endDate.getTime());
        dest.writeString(legalTermsUrl);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Campaign)) {
            return false;
        }

        final Campaign campaign = (Campaign) o;

        return id.equals(campaign.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getLegalTermsUrl() {
        return legalTermsUrl;
    }

    @SuppressWarnings("unused")
    public static final class Builder {

        //region mandatory params
        /* default */ @NonNull final String id;
        //endregion mandatory params

        /* default */ BigDecimal maxCouponAmount = BigDecimal.ZERO;
        /* default */ @Nullable String codeType;
        /* default */ int maxRedeemPerUser = 1;
        /* default */ @Nullable Date endDate;
        /* default */ @Nullable String legalTermsUrl;

        /**
         * Builder for campaign construction.
         * This campaign have to be created in Mercado Pago.
         *
         * @param id campaign id
         */
        public Builder(@NonNull final String id) {
            this.id = id;
        }

        /**
         * When the campaign has amount cap per discount you should set it here
         * to communicate to the user that it exists.
         *
         * @param maxCouponAmount amount to be shown in message.
         * @return builder
         */
        public Builder setMaxCouponAmount(final BigDecimal maxCouponAmount) {
            this.maxCouponAmount = maxCouponAmount;
            return this;
        }

        /**
         * Code type describes the kind of discount related with this campaign.
         *
         * @param codeType 'single', 'multiple' or null.
         * @return builder
         */
        public Builder setCodeType(@Nullable final String codeType) {
            this.codeType = codeType;
            return this;
        }

        /**
         * This value represents how many times this discount will be applied.
         * By default this value will be 1.
         *
         * @param maxRedeemPerUser amount of times that will apply.
         * @return builder.
         */
        public Builder setMaxRedeemPerUser(final int maxRedeemPerUser) {
            this.maxRedeemPerUser = maxRedeemPerUser;
            return this;
        }

        /**
         * This value represents discount campaign's end date (expiry date).
         *
         * @param endDate for discount campaign.
         * @return builder.
         */
        public Builder setEndDate(final Date endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * This string represents discount campaign's terms and conditions URL.
         *
         * @param legalTermsUrl for discount campaign.
         * @return builder.
         */
        public Builder setLegalTermsUrl(final String legalTermsUrl) {
            this.legalTermsUrl = legalTermsUrl;
            return this;
        }

        /**
         * It creates the Campaign.
         *
         * @return Campaign
         */
        public Campaign build() {
            return new Campaign(this);
        }
    }
}
