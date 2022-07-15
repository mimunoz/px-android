package com.mercadopago.android.px.internal.features.payment_result.components;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultLegacyViewModel;
import com.mercadopago.android.px.internal.view.ActionDispatcher;

public final class PaymentResultLegacyRenderer {

    private PaymentResultLegacyRenderer() {
    }

    public static void render(@NonNull final ViewGroup parent, @NonNull final ActionDispatcher callback,
        @NonNull final PaymentResultLegacyViewModel viewModel) {
        final Body bodyComponent = getBodyComponent(viewModel, callback);
        if (bodyComponent.hasSomethingToDraw()) {
            parent.findViewById(R.id.body).setVisibility(View.GONE);
            getBodyComponent(viewModel, callback).render(parent.findViewById(R.id.legacy_body));
        }

        parent.addView(new FooterPaymentResult(viewModel.model.getPaymentResult(), callback).render(parent));
    }

    private static Body getBodyComponent(@NonNull final PaymentResultLegacyViewModel viewModel,
        @NonNull final ActionDispatcher callback) {
        final PaymentResultBodyProps bodyProps =
            new PaymentResultBodyProps.Builder(viewModel.configuration)
                .setPaymentResult(viewModel.model.getPaymentResult())
                .setInstruction(viewModel.instruction)
                .setCurrency(viewModel.model.getCurrency())
                .build();
        return new Body(bodyProps, callback);
    }
}