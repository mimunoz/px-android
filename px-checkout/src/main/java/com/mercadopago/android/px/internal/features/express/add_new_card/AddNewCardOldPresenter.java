package com.mercadopago.android.px.internal.features.express.add_new_card;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.services.Callback;
import java.util.List;

public class AddNewCardOldPresenter extends OtherPaymentMethodPresenter {

    private static final String TYPE_TO_DRIVE = "cards";

    private final InitRepository initRepository;

    /* default */ AddNewCardOldPresenter(@NonNull final InitRepository initRepository) {
        super(null, null, null);
        this.initRepository = initRepository;
    }

    @Override
    public void onAddNewCardSelected() {
        initRepository.init().enqueue(new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                getView().showPaymentMethods(getCardsGroup(initResponse.getGroups()));
            }

            @Override
            public void failure(final ApiException apiException) {
                throw new IllegalStateException("AddNewCardPresenter could not retrieve PaymentMethodSearch");
            }
        });
    }

    @Nullable
        /* default */ PaymentMethodSearchItem getCardsGroup(@NonNull final List<PaymentMethodSearchItem> groups) {
        for (final PaymentMethodSearchItem paymentMethodSearchItem : groups) {
            if (TYPE_TO_DRIVE.equalsIgnoreCase(paymentMethodSearchItem.getId())) {
                return paymentMethodSearchItem;
            }
        }
        return null;
    }
}