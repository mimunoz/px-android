package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.viewmodel.ConfirmButtonViewModel;
import com.mercadopago.android.px.model.ExpressMetadata;

public class ConfirmButtonViewModelMapper extends Mapper<ExpressMetadata, ConfirmButtonViewModel> {
    private final DisabledPaymentMethodRepository disabledPaymentMethodRepository;

    public ConfirmButtonViewModelMapper(
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository) {
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
    }

    @Override
    public ConfirmButtonViewModel map(@NonNull final ExpressMetadata val) {
        return new ConfirmButtonViewModel(
            val.isNewCard() || val.isOfflineMethods() ||
                disabledPaymentMethodRepository.hasPaymentMethodId(val.getCustomOptionId()));
    }
}