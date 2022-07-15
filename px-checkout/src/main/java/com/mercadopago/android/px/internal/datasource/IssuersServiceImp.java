package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.repository.IssuersRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.services.IssuersService;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.ProcessingMode;
import java.util.List;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;

public class IssuersServiceImp implements IssuersRepository {

    @NonNull private final IssuersService issuersService;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final UserSelectionRepository userSelectionRepository;

    public IssuersServiceImp(@NonNull final IssuersService issuersService,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final UserSelectionRepository userSelectionRepository) {
        this.issuersService = issuersService;
        this.paymentSettingRepository = paymentSettingRepository;
        this.userSelectionRepository = userSelectionRepository;
    }

    @Override
    public MPCall<List<Issuer>> getIssuers(final String paymentMethodId, final String bin) {
        final ProcessingMode[] processingModes = userSelectionRepository.getPaymentMethod().getProcessingModes();
        return issuersService.getIssuers(API_ENVIRONMENT, paymentSettingRepository.getPublicKey(),
            paymentSettingRepository.getPrivateKey(), paymentMethodId, bin,
            ProcessingMode.asCommaSeparatedQueryParam(processingModes));
    }
}