package com.mercadopago.android.px.internal.di;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.core.ApplicationModule;
import com.mercadopago.android.px.internal.datasource.CardAssociationGatewayService;
import com.mercadopago.android.px.internal.datasource.CardAssociationService;
import com.mercadopago.android.px.internal.datasource.CardPaymentMethodService;
import com.mercadopago.android.px.internal.repository.CardPaymentMethodRepository;
import com.mercadopago.android.px.internal.services.CardService;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.util.RetrofitUtil;
import com.mercadopago.android.px.model.Device;

public final class CardAssociationSession extends ApplicationModule {

    /**
     * This singleton instance is safe because session will work with application context. Application context it's
     * never leaking.
     */
    @SuppressLint("StaticFieldLeak") private static CardAssociationSession instance;
    private final NetworkModule networkModule;

    private CardAssociationSession(@NonNull final Context context) {
        super(context.getApplicationContext());
        networkModule = NetworkModule.INSTANCE;
    }

    public static CardAssociationSession getCardAssociationSession(final Context context) {
        if (instance == null) {
            instance = new CardAssociationSession(context);
        }
        return instance;
    }

    @NonNull
    public CardAssociationService getCardAssociationService() {
        return new CardAssociationService(networkModule.getRetrofitClient().create(CardService.class));
    }

    @NonNull
    public CardPaymentMethodRepository getCardPaymentMethodRepository() {
        return new CardPaymentMethodService(networkModule.getRetrofitClient().create(
            com.mercadopago.android.px.internal.services.PaymentService.class));
    }

    @NonNull
    public ESCManagerBehaviour getMercadoPagoESC() {
        //noinspection ConstantConditions
        return BehaviourProvider
            .getEscManagerBehaviour(networkModule.getSessionIdProvider().getSessionId(),
                networkModule.getFlowIdProvider().getFlowId());
    }

    @NonNull
    public CardAssociationGatewayService getGatewayService() {
        return new CardAssociationGatewayService(
            RetrofitUtil.getRetrofitClient(getApplicationContext()).create(GatewayService.class),
            new Device(getApplicationContext()));
    }
}