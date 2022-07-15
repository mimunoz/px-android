package com.mercadopago.android.px.internal.features.review_and_confirm.models;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Site;
import java.math.BigDecimal;
import java.util.List;

public class SummaryModel implements Parcelable {

    public static final Creator<SummaryModel> CREATOR = new Creator<SummaryModel>() {
        @Override
        public SummaryModel createFromParcel(final Parcel in) {
            return new SummaryModel(in);
        }

        @Override
        public SummaryModel[] newArray(final int size) {
            return new SummaryModel[size];
        }
    };

    public static String resolveTitle(@NonNull final List<Item> items, @NonNull final Resources resources) {
        final int quantity = items.size() == 1 ? items.get(0).getQuantity() : items.size();
        return quantity == 1 && TextUtil.isNotEmpty(items.get(0).getTitle()) ?
            items.get(0).getTitle() : (resources.getString(quantity == 1 ?
            R.string.px_review_summary_product : R.string.px_review_summary_products));
    }

    @NonNull private final String amount;
    @NonNull private final Site site;
    @NonNull private final Currency currency;
    @NonNull private final String paymentTypeId;
    @Nullable private final String payerCostTotalAmount;
    private final int installments;
    private final String cftPercent;
    private final String couponAmount;
    private final boolean hasPercentOff;
    private final String installmentsRate;
    private final String installmentAmount;
    public final String title;
    private final String itemsAmount;
    private final String charges;

    public SummaryModel(final BigDecimal amount,
        final PaymentMethod paymentMethod,
        @NonNull final Site site,
        @NonNull final Currency currency,
        final PayerCost payerCost,
        final Discount discount,
        final String title,
        final BigDecimal itemsAmount,
        final BigDecimal charges) {

        this.amount = amount.toString();
        this.site = site;
        this.currency = currency;
        paymentTypeId = paymentMethod.getPaymentTypeId();
        payerCostTotalAmount =
            payerCost != null && payerCost.getTotalAmount() != null ? payerCost.getTotalAmount().toString() : null;
        installments = payerCost != null && payerCost.getInstallments() != null ? payerCost.getInstallments() : 1;
        cftPercent = payerCost != null && payerCost.getCFTPercent() != null ? payerCost.getCFTPercent() : null;
        couponAmount = discount != null ? discount.getCouponAmount().toString() : null;
        hasPercentOff = discount != null && discount.hasPercentOff();
        installmentsRate =
            payerCost != null && payerCost.getInstallmentRate() != null ? payerCost.getInstallmentRate().toString()
                : null;
        installmentAmount =
            payerCost != null && payerCost.getInstallmentAmount() != null ? payerCost.getInstallmentAmount().toString()
                : null;
        this.title = title;
        this.itemsAmount = itemsAmount.toString();
        this.charges = charges.toString();
    }

    protected SummaryModel(final Parcel in) {
        amount = in.readString();
        site = in.readParcelable(Site.class.getClassLoader());
        currency = in.readParcelable(Currency.class.getClassLoader());
        paymentTypeId = in.readString();
        payerCostTotalAmount = in.readString();
        installments = in.readInt();
        cftPercent = in.readString();
        couponAmount = in.readString();
        hasPercentOff = in.readByte() != 0;
        installmentsRate = in.readString();
        installmentAmount = in.readString();
        title = in.readString();
        itemsAmount = in.readString();
        charges = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(amount);
        dest.writeParcelable(site, flags);
        dest.writeParcelable(currency, flags);
        dest.writeString(paymentTypeId);
        dest.writeString(payerCostTotalAmount);
        dest.writeInt(installments);
        dest.writeString(cftPercent);
        dest.writeString(couponAmount);
        dest.writeByte((byte) (hasPercentOff ? 1 : 0));
        dest.writeString(installmentsRate);
        dest.writeString(installmentAmount);
        dest.writeString(title);
        dest.writeString(itemsAmount);
        dest.writeString(charges);
    }

    @NonNull
    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public String getCftPercent() {
        return cftPercent;
    }

    public BigDecimal getAmountToPay() {
        return new BigDecimal(amount);
    }

    public BigDecimal getItemsAmount() {
        return new BigDecimal(itemsAmount);
    }

    @Nullable
    public BigDecimal getPayerCostTotalAmount() {
        return payerCostTotalAmount != null ? new BigDecimal(payerCostTotalAmount) : null;
    }

    @Nullable
    public BigDecimal getCouponAmount() {
        return couponAmount != null ? new BigDecimal(couponAmount) : null;
    }

    @Nullable
    public BigDecimal getInstallmentsRate() {
        return installmentsRate != null ? new BigDecimal(installmentsRate) : null;
    }

    @Nullable
    public BigDecimal getInstallmentAmount() {
        return installmentAmount != null ? new BigDecimal(installmentAmount) : null;
    }

    @NonNull
    public Site getSite() {
        return site;
    }

    @NonNull
    public Currency getCurrency() {
        return currency;
    }

    @NonNull
    public BigDecimal getCharges() {
        return new BigDecimal(charges);
    }

    public int getInstallments() {
        return installments;
    }

    public boolean hasMultipleInstallments() {
        return getInstallments() > 1;
    }

    public boolean hasCoupon() {
        return getCouponAmount() != null;
    }

    public boolean hasCharges() {
        return BigDecimal.ZERO.compareTo(getCharges()) != 0;
    }
}

