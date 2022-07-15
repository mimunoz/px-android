package com.mercadopago.android.px.model.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.mercadopago.android.px.addons.model.internal.Experiment;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PayerCompliance;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.List;
import java.util.Map;

public final class InitResponse extends PaymentMethodSearch {

    private CheckoutPreference preference;

    private Site site;

    private Currency currency;

    private List<Experiment> experiments;

    private PayerCompliance payerCompliance;

    @SerializedName("configurations")
    private Configuration configuration;

    private Map<String, Modal> modals;

    @Nullable
    public CheckoutPreference getCheckoutPreference() {
        return preference;
    }

    @NonNull
    public Site getSite() {
        return site;
    }

    @NonNull
    public Currency getCurrency() {
        return currency;
    }

    @Nullable
    public List<Experiment> getExperiments() {
        return experiments;
    }

    @Nullable
    public PayerCompliance getPayerCompliance() {
        return payerCompliance;
    }

    @NonNull
    public Configuration getConfiguration() {
        return configuration;
    }

    @NonNull
    public Map<String, Modal> getModals() {
        return modals;
    }
}