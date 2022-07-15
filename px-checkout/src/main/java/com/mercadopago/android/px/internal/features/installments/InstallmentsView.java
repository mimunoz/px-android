package com.mercadopago.android.px.internal.features.installments;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentRowHolder;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.exceptions.ApiException;
import java.math.BigDecimal;
import java.util.List;

public interface InstallmentsView extends MvpView {

    void showApiErrorScreen(final ApiException apiException, final String requestOrigin);

    void showInstallments(final List<InstallmentRowHolder.Model> models);

    void finishWithResult();

    void showLoadingView();

    void hideLoadingView();

    void showErrorNoPayerCost();

    void warnAboutBankInterests();

    void showDetailDialog(@NonNull final Currency currency, @NonNull final DiscountConfigurationModel discountModel);

    void showAmount(@NonNull final DiscountConfigurationModel discountModel, @NonNull final BigDecimal itemsPlusCharges,
        @NonNull final Currency currency);

    void hideAmountRow();

    void hideCardContainer();
}
