package com.mercadopago.android.px.preferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.DifferentialPricing;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.OpenPayer;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.ProcessingMode;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.exceptions.CheckoutPreferenceException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Model that represents curl -X OPTIONS "https://api.mercadopago.com/checkout/preferences" | json_pp It can be not
 * exactly the same because exists custom configurations for open Preference. Some values like: binary mode are not
 * present on API call.
 */
@SuppressWarnings("unused")
public class CheckoutPreference implements Serializable {

    /**
     * When the preference comes from backend then id is received - Custom created CheckoutPreferences have null id. it
     * is nullable but it shouldn't
     */
    @SuppressWarnings("UnusedDeclaration")
    @Nullable private String id;

    @SuppressWarnings("UnusedDeclaration")
    @NonNull private final String siteId;

    @NonNull private final List<Item> items;

    @NonNull private final Payer payer;

    @SerializedName("differential_pricing")
    @Nullable private final DifferentialPricing differentialPricing;

    @SerializedName("payment_methods")
    @Nullable private final PaymentPreference paymentPreference;

    @Nullable private final Date expirationDateTo;

    @Nullable private final Date expirationDateFrom;

    @Nullable private final String collectorId;

    @NonNull private final String marketplace;

    //region support external integrations - payment processor instores
    @Nullable private final BigDecimal marketplaceFee;

    @Nullable private final BigDecimal shippingCost;

    @Nullable private final String operationType;

    @Nullable private final BigDecimal conceptAmount;

    @Nullable private final String conceptId;

    @Nullable private final String additionalInfo;

    @Nullable private final String branchId;

    @Nullable private final ProcessingMode[] processingModes;

    @Nullable private final Long orderId;

    @Nullable private final Long merchantOrderId;

    @SerializedName("binary_mode")
    private boolean isBinaryMode = false;
    //endregion support external integrations

    /* default */ CheckoutPreference(final Builder builder) {
        items = builder.items;
        expirationDateFrom = builder.expirationDateFrom;
        expirationDateTo = builder.expirationDateTo;
        siteId = builder.site.getId();
        marketplace = builder.marketplace;
        marketplaceFee = builder.marketplaceFee;
        shippingCost = builder.shippingCost;
        operationType = builder.operationType;
        differentialPricing = builder.differentialPricing;
        conceptAmount = builder.conceptAmount;
        conceptId = builder.conceptId;
        payer = builder.payer;
        isBinaryMode = builder.isBinaryMode;
        additionalInfo = builder.additionalInfo;
        processingModes = builder.processingModes;
        collectorId = null;
        orderId = null;
        merchantOrderId = null;

        branchId = builder.branchId;
        paymentPreference = new PaymentPreference();
        paymentPreference.setExcludedPaymentTypeIds(builder.excludedPaymentTypes);
        paymentPreference.setExcludedPaymentMethodIds(builder.excludedPaymentMethods);
        paymentPreference.setMaxAcceptedInstallments(builder.maxInstallments);
        paymentPreference.setDefaultInstallments(builder.defaultInstallments);
    }

    /**
     * @throws CheckoutPreferenceException
     * @deprecated preference is validated backend side
     */
    @Deprecated
    public void validate() throws CheckoutPreferenceException {
    }

    //region support external integrations - payment processor instores

    @Nullable
    public String getOperationType() {
        return operationType;
    }

    @Nullable
    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    @Nullable
    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    @Nullable
    public DifferentialPricing getDifferentialPricing() {
        return differentialPricing;
    }

    @Nullable
    public BigDecimal getConceptAmount() {
        return conceptAmount;
    }

    @Nullable
    public String getConceptId() {
        return conceptId;
    }

    //endregion support external integrations

    /**
     * Sum of value * quantity of listed items in a preference.
     *
     * @return items total amount
     */
    @NonNull
    public BigDecimal getTotalAmount() {
        return Item.getTotalAmountWith(items);
    }

    /**
     * @return site
     * @deprecated preference should not have site in it's model
     */
    @Deprecated
    @Nullable
    public Site getSite() {
        return TextUtil.isNotEmpty(siteId) ? Sites.getById(siteId) : null;
    }

    @Size(min = 1)
    @NonNull
    public List<Item> getItems() {
        return items;
    }

    @NonNull
    public Payer getPayer() {
        return payer;
    }

    @Nullable
    public Date getExpirationDateFrom() {
        return expirationDateFrom;
    }

    @Nullable
    public Date getExpirationDateTo() {
        return expirationDateTo;
    }

    @Nullable
    public String getCollectorId() {
        return collectorId;
    }

    @Nullable
    public String getDefaultPaymentMethodId() {
        return getPaymentPreference().getDefaultPaymentMethodId();
    }

    @Nullable
    public Integer getDefaultInstallments() {
        return getPaymentPreference().getDefaultInstallments();
    }

    @Nullable
    public Integer getMaxInstallments() {
        return getPaymentPreference().getMaxInstallments();
    }

    @NonNull
    public List<String> getExcludedPaymentTypes() {
        return getPaymentPreference().getExcludedPaymentTypes();
    }

    @NonNull
    public List<String> getExcludedPaymentMethods() {
        return getPaymentPreference().getExcludedPaymentMethodIds();
    }

    @Nullable
    public String getBranchId() {
        return branchId;
    }

    @NonNull
    public ProcessingMode[] getProcessingModes() {
        // when comes from backend this value can be null.
        final ProcessingMode[] defaultProcessingMode = { ProcessingMode.AGGREGATOR };
        return processingModes == null || processingModes.length == 0 ? defaultProcessingMode : processingModes;
    }

    @NonNull
    public String getMarketplace() {
        return marketplace;
    }

    @NonNull
    public PaymentPreference getPaymentPreference() {
        // If payment preference does not exists create one.
        return paymentPreference == null ? new PaymentPreference() : paymentPreference;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public boolean isBinaryMode() {
        return isBinaryMode;
    }

    @Nullable
    public Long getOrderId() {
        return orderId;
    }

    @Nullable
    public Long getMerchantOrderId() {
        return merchantOrderId;
    }

    @NonNull
    @Override
    public String toString() {
        return "CheckoutPreference{" +
            "id='" + id + '\'' +
            ", siteId='" + siteId + '\'' +
            ", items=" + items +
            ", payer=" + payer +
            ", differentialPricing=" + differentialPricing +
            ", paymentPreference=" + paymentPreference +
            ", expirationDateTo=" + expirationDateTo +
            ", expirationDateFrom=" + expirationDateFrom +
            ", marketplace='" + marketplace + '\'' +
            ", marketplaceFee=" + marketplaceFee +
            ", shippingCost=" + shippingCost +
            ", operationType='" + operationType + '\'' +
            ", conceptAmount=" + conceptAmount +
            ", conceptId='" + conceptId + '\'' +
            ", isBinaryMode=" + isBinaryMode +
            '}';
    }

    public static class Builder {

        private static final String DEFAULT_MARKETPLACE = "NONE";

        //region mandatory params
        /* default */ final List<Item> items;
        /* default */ final Site site;
        //endregion mandatory params

        /* default */ final List<String> excludedPaymentMethods;
        /* default */ final List<String> excludedPaymentTypes;
        /* default */ ProcessingMode[] processingModes = { ProcessingMode.AGGREGATOR };
        /* default */ Integer maxInstallments;
        /* default */ Integer defaultInstallments;
        /* default */ Date expirationDateTo;
        /* default */ Date expirationDateFrom;
        /* default */ String marketplace;
        /* default */ BigDecimal marketplaceFee;
        /* default */ BigDecimal shippingCost;
        /* default */ String operationType;
        /* default */ @Nullable DifferentialPricing differentialPricing;
        /* default */ BigDecimal conceptAmount;
        /* default */ String conceptId;
        /* default */ boolean isBinaryMode = false;
        /* default */ final Payer payer;
        /* default */ String additionalInfo;
        /* default */ @Nullable String branchId;

        /**
         * Builder for custom CheckoutPreference construction. It should be only used if you are processing the payment
         * with a Payment processor. Otherwise you should use the ID constructor.
         *
         * @param site preference site {@link Sites#getById(String)}
         * @param payerEmail payer email
         * @param items items to pay
         */
        public Builder(@NonNull final Site site, @NonNull final String payerEmail,
            @Size(min = 1) @NonNull final List<Item> items) {
            this.items = items;
            payer = new Payer();
            payer.setEmail(payerEmail);
            this.site = site;
            excludedPaymentMethods = new ArrayList<>();
            excludedPaymentTypes = new ArrayList<>();
            marketplace = DEFAULT_MARKETPLACE;
        }

        /**
         * Builder for custom CheckoutPreference construction. It should be only used if you are processing the payment
         * with a Payment processor. Otherwise you should use the ID constructor.
         *
         * @param site preference site {@link Sites#getById(String)}
         * @param payer payer
         * @param items items to pay
         */
        @Deprecated
        public Builder(@NonNull final Site site, @NonNull final OpenPayer payer,
            @Size(min = 1) @NonNull final List<Item> items) {
            this.items = items;
            this.payer = payer;
            this.site = site;
            excludedPaymentMethods = new ArrayList<>();
            excludedPaymentTypes = new ArrayList<>();
            marketplace = DEFAULT_MARKETPLACE;
        }

        /**
         * Add exclusion payment method id If you exclude it, it's not going appear as a payment method available on
         * checkout
         *
         * @param paymentMethodId exclusion id
         * @return builder
         * @see com.mercadopago.android.px.model.PaymentMethods
         */
        public Builder addExcludedPaymentMethod(@NonNull final String paymentMethodId) {
            excludedPaymentMethods.add(paymentMethodId);
            return this;
        }

        /**
         * Add exclusion list by payment method id If you exclude it, it's not going appear as a payment method
         * available on checkout
         *
         * @param paymentMethodIds exclusion list
         * @return builder
         * @see com.mercadopago.android.px.model.PaymentMethods
         */
        public Builder addExcludedPaymentMethods(@NonNull final Collection<String> paymentMethodIds) {
            excludedPaymentMethods.addAll(paymentMethodIds);
            return this;
        }

        /**
         * Add exclusion by payment type If you exclude it, it's not going appear as a payment method available on
         * checkout
         *
         * @param paymentTypeId exclusion type
         * @return builder
         * @see com.mercadopago.android.px.model.PaymentTypes
         */
        public Builder addExcludedPaymentType(@NonNull final String paymentTypeId) {
            excludedPaymentTypes.add(paymentTypeId);
            return this;
        }

        /**
         * Add exclusion list by payment type If you exclude it, it's not going appear as a payment method available on
         * checkout
         *
         * @param paymentTypeIds exclusion list
         * @return builder
         * @see com.mercadopago.android.px.model.PaymentTypes
         */
        public Builder addExcludedPaymentTypes(@NonNull final Collection<String> paymentTypeIds) {
            excludedPaymentTypes.addAll(paymentTypeIds);
            return this;
        }

        /**
         * If enableBinaryMode is called, processed payment can only be APPROVED or REJECTED. Default value is false.
         * <p>
         * Non compatible with PaymentProcessor.
         * <p>
         * Non compatible with off payments methods
         *
         * @return builder to keep operating
         */
        public Builder setBinaryMode(final boolean isBinaryMode) {
            this.isBinaryMode = isBinaryMode;
            return this;
        }

        /**
         * This value limits the amount of installments to be shown by the user.
         *
         * @param maxInstallments max installments to be shown
         * @return builder
         */
        public Builder setMaxInstallments(@Nullable final Integer maxInstallments) {
            this.maxInstallments = maxInstallments;
            return this;
        }

        /**
         * When default installments is not null then this value will be forced as installment selected if it matches
         * with one provided by the Installments service.
         *
         * @param defaultInstallments number of the value to be forced
         * @return builder
         */
        public Builder setDefaultInstallments(@Nullable final Integer defaultInstallments) {
            this.defaultInstallments = defaultInstallments;
            return this;
        }

        /**
         * Date that indicates when this preference expires. If the preference is expired, then the checkout will show
         * an error.
         *
         * @param date creation date.
         * @return builder
         */
        public Builder setExpirationDate(@Nullable final Date date) {
            expirationDateTo = date;
            return this;
        }

        /**
         * Date that indicates from when the preference is active. If the preference is related with a date in the
         * future then an error screen will be shown.
         *
         * @param date creation date.
         * @return builder
         */
        public Builder setActiveFrom(@Nullable final Date date) {
            expirationDateFrom = date;
            return this;
        }

        /**
         * Differential pricing configuration for this preference. This object is related with the way the installments
         * are asked.
         *
         * @param differentialPricing differential pricing object
         * @return builder
         */
        public Builder setDifferentialPricing(@Nullable final DifferentialPricing differentialPricing) {
            this.differentialPricing = differentialPricing;
            return this;
        }

        /**
         * internal usage
         *
         * @param marketplace origin of the payment. Default value: NONE.
         * @return builder
         */
        public Builder setMarketplace(final String marketplace) {
            this.marketplace = marketplace;
            return this;
        }

        /**
         * internal usage
         *
         * @param marketplaceFee amount fee
         * @return builder
         */
        public Builder setMarketplaceFee(final BigDecimal marketplaceFee) {
            this.marketplaceFee = marketplaceFee;
            return this;
        }

        /**
         * internal usage
         *
         * @param shippingCost amount fee
         * @return builder
         */
        public Builder setShippingCost(final BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        /**
         * internal usage
         *
         * @param operationType this operation can be ...
         * @return builder
         */
        public Builder setOperationType(final String operationType) {
            this.operationType = operationType;
            return this;
        }

        /**
         * internal usage
         *
         * @param conceptAmount amount
         * @return builder
         */
        public Builder setConceptAmount(final BigDecimal conceptAmount) {
            this.conceptAmount = conceptAmount;
            return this;
        }

        /**
         * internal usage
         *
         * @param conceptId identifier
         * @return builder
         */
        public Builder setConceptId(final String conceptId) {
            this.conceptId = conceptId;
            return this;
        }

        /**
         * internal usage
         *
         * @param additionalInfo identifier
         * @return builder
         */
        public Builder setAdditionalInfo(@NonNull final String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        /**
         * External id that will be pass through installments to define custom payment method agreements for this id.
         *
         * @param branchId custom branch id for this payment.
         */
        public Builder setBranchId(@Nullable final String branchId) {
            this.branchId = branchId;
            return this;
        }

        /**
         * Processing mode allowed for this payment. Can be any of {@link ProcessingMode} values.
         *
         * @param processingModes allowed for this payment preference.
         */
        public Builder setProcessingModes(@NonNull final ProcessingMode[] processingModes) {
            this.processingModes = processingModes;
            return this;
        }

        /**
         * It creates the checkout preference.
         *
         * @return CheckoutPreference
         */
        public CheckoutPreference build() {
            return new CheckoutPreference(this);
        }
    }
}