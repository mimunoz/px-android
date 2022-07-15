package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentRowHolder;
import com.mercadopago.android.px.internal.util.BenefitsHelper;
import com.mercadopago.android.px.model.BenefitsMetadata;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.internal.Text;

public class InstallmentViewModelMapper extends Mapper<PayerCost, InstallmentRowHolder.Model> {

    private final Currency currency;
    private final BenefitsMetadata benefits;

    public InstallmentViewModelMapper(@NonNull final Currency currency, @Nullable final BenefitsMetadata benefits) {
        this.currency = currency;
        this.benefits = benefits;
    }

    @Override
    public InstallmentRowHolder.Model map(@NonNull final PayerCost val) {
        final int installments = val.getInstallments();
        final Text interestFreeText = BenefitsHelper.getInterestFreeText(benefits, installments);
        final Text reimbursementText = BenefitsHelper.getReimbursementText(benefits, installments);
        final boolean showBigRow = benefits != null && benefits.getReimbursement() != null;
        return new InstallmentRowHolder.Model(val, currency, interestFreeText,
            reimbursementText, showBigRow);
    }
}
