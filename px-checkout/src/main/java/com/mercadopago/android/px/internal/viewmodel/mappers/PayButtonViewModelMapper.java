package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.configuration.CustomStringConfiguration;
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel;

public class PayButtonViewModelMapper extends Mapper<CustomStringConfiguration, PayButtonViewModel> {

    @Override
    public PayButtonViewModel map(@NonNull final CustomStringConfiguration configuration) {
        return new PayButtonViewModel(configuration.getCustomPayButtonText(),
            configuration.getCustomPayButtonProgressText());
    }
}