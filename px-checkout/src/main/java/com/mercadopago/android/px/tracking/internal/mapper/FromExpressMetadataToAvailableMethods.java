package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.mappers.NonNullMapper;
import com.mercadopago.android.px.model.AccountMoneyMetadata;
import com.mercadopago.android.px.model.BenefitsMetadata;
import com.mercadopago.android.px.model.CardMetadata;
import com.mercadopago.android.px.model.ExpressMetadata;
import com.mercadopago.android.px.tracking.internal.model.AccountMoneyExtraInfo;
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod;
import com.mercadopago.android.px.tracking.internal.model.CardExtraExpress;
import java.util.Set;

public class FromExpressMetadataToAvailableMethods extends NonNullMapper<ExpressMetadata, AvailableMethod> {

    @NonNull private final Set<String> cardsWithEsc;
    @NonNull private final Set<String> cardsWithSplit;

    public FromExpressMetadataToAvailableMethods(@NonNull final Set<String> cardsWithEsc,
        @NonNull final Set<String> cardsWithSplit) {
        this.cardsWithEsc = cardsWithEsc;
        this.cardsWithSplit = cardsWithSplit;
    }

    @Override
    public AvailableMethod map(@NonNull final ExpressMetadata expressMetadata) {
        boolean hasInterestFree = false;
        boolean hasReimbursement = false;
        final BenefitsMetadata benefits = expressMetadata.getBenefits();

        if (benefits != null) {
            hasInterestFree = benefits.getInterestFree() != null;
            hasReimbursement = benefits.getReimbursement() != null;
        }

        final AvailableMethod.Builder builder = new AvailableMethod.Builder(
            expressMetadata.getPaymentMethodId(),
            expressMetadata.getPaymentTypeId(),
            hasInterestFree, hasReimbursement);

        if (expressMetadata.isCard()) {
            final CardMetadata card = expressMetadata.getCard();

            builder.setExtraInfo(
                CardExtraExpress
                    .expressSavedCard(card, cardsWithEsc.contains(card.getId()), cardsWithSplit.contains(card.getId()))
                    .toMap()
            );

        } else if (expressMetadata.getAccountMoney() != null) {
            final AccountMoneyMetadata accountMoney = expressMetadata.getAccountMoney();

            builder.setExtraInfo(
                new AccountMoneyExtraInfo(accountMoney.getBalance(), accountMoney.isInvested()).toMap()
            );

        } else if (expressMetadata.isNewCard()) {
            return null;
        }

        return builder.build();
    }
}
