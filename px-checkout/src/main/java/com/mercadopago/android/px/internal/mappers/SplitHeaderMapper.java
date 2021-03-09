package com.mercadopago.android.px.internal.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.features.express.slider.SplitPaymentHeaderAdapter;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.internal.OneTapItem;

public class SplitHeaderMapper extends Mapper<OneTapItem, SplitPaymentHeaderAdapter.Model> {

    @NonNull private final Currency currency;
    @NonNull private final AmountConfigurationRepository amountConfigurationRepository;

    public SplitHeaderMapper(@NonNull final Currency currency,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository) {
        this.currency = currency;
        this.amountConfigurationRepository = amountConfigurationRepository;
    }

    @Override
    public SplitPaymentHeaderAdapter.Model map(@NonNull final OneTapItem val) {
        if (val.isCard() && val.getStatus().isEnabled()) {
            final String cardId = val.getCard().getId();
            final AmountConfiguration config =
                amountConfigurationRepository.getConfigurationSelectedFor(cardId);
            return config.allowSplit() ? new SplitPaymentHeaderAdapter.SplitModel(currency,
                config.getSplitConfiguration())
                : new SplitPaymentHeaderAdapter.Empty();
        }
        return new SplitPaymentHeaderAdapter.Empty();
    }
}