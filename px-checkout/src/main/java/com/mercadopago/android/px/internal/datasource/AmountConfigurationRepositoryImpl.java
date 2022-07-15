package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.PaymentTypes;

public class AmountConfigurationRepositoryImpl implements AmountConfigurationRepository {

    private final UserSelectionRepository userSelectionRepository;
    /* default */ @Nullable ConfigurationSolver configurationSolver;

    public AmountConfigurationRepositoryImpl(@NonNull final InitRepository initRepository,
        @NonNull final UserSelectionRepository userSelectionRepository) {
        this.userSelectionRepository = userSelectionRepository;
        initRepository.addOnChangedListener(initResponse -> configurationSolver =
            new ConfigurationSolverImpl(initResponse.getDefaultAmountConfiguration(),
                initResponse.getCustomSearchItems()));
    }

    @NonNull
    @Override
    public AmountConfiguration getCurrentConfiguration() throws IllegalStateException {
        if (userSelectionRepository.getCard() != null) { // Saved card
            return configurationSolver.getAmountConfigurationFor(userSelectionRepository.getCard().getId());
        } else if (PaymentTypes.isAccountMoney(userSelectionRepository.getPaymentMethod().getPaymentTypeId()) ||
            PaymentTypes.isDigitalCurrency(userSelectionRepository.getPaymentMethod().getPaymentTypeId())) {
            return configurationSolver.getAmountConfigurationFor(userSelectionRepository.getPaymentMethod().getId());
        } else {
            throw new IllegalStateException(
                "Payer costs shouldn't be requested without a selected card, credit or account money");
        }
    }

    @Nullable
    @Override
    public AmountConfiguration getConfigurationFor(@NonNull final String customOptionId) {
        return configurationSolver
            .getAmountConfigurationFor(customOptionId, configurationSolver.getConfigurationHashFor(customOptionId));
    }
}