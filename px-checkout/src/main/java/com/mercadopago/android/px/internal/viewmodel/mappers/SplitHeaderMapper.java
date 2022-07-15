package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.express.slider.SplitPaymentHeaderAdapter;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.ExpressMetadata;

public class SplitHeaderMapper extends Mapper<ExpressMetadata, SplitPaymentHeaderAdapter.Model> {

    @NonNull private final Currency currency;
    @NonNull private final AmountConfigurationRepository amountConfigurationRepository;

    public SplitHeaderMapper(@NonNull final Currency currency,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository) {
        this.currency = currency;
        this.amountConfigurationRepository = amountConfigurationRepository;
    }

    @Override
    public SplitPaymentHeaderAdapter.Model map(@NonNull final ExpressMetadata val) {
        if (val.isCard() && val.getStatus().isEnabled()) {
            final AmountConfiguration config =
                amountConfigurationRepository.getConfigurationFor(val.getCard().getId());
            return config.allowSplit() ? new SplitPaymentHeaderAdapter.SplitModel(currency,
                config.getSplitConfiguration())
                : new SplitPaymentHeaderAdapter.Empty();
        }
        return new SplitPaymentHeaderAdapter.Empty();
    }
}