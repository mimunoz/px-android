package com.mercadopago.android.px.internal.features.express.add_new_card;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.core.FlowIdProvider;
import com.mercadopago.android.px.internal.core.SessionIdProvider;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.CardFormWithFragmentWrapper;

public class OtherPaymentMethodPresenter extends BasePresenter<AddNewCard.View> implements AddNewCard.Actions {

    private final PaymentSettingRepository settingRepository;
    private final SessionIdProvider sessionIdProvider;
    @NonNull private final FlowIdProvider flowIdProvider;

    /* default */ OtherPaymentMethodPresenter(@NonNull final PaymentSettingRepository settingRepository,
        @NonNull final SessionIdProvider sessionIdProvider, @NonNull final FlowIdProvider flowIdProvider) {
        this.settingRepository = settingRepository;
        this.sessionIdProvider = sessionIdProvider;
        this.flowIdProvider = flowIdProvider;
    }

    @Override
    public void onAddNewCardSelected() {
        getView().startCardForm(new CardFormWithFragmentWrapper(settingRepository, sessionIdProvider, flowIdProvider));
    }
}