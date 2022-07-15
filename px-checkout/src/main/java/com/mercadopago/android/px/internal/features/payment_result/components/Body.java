package com.mercadopago.android.px.internal.features.payment_result.components;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory;
import com.mercadopago.android.px.internal.features.payment_result.props.BodyErrorProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsProps;
import com.mercadopago.android.px.internal.features.payment_result.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.viewmodel.PaymentResultViewModel;

public class Body extends CompactComponent<PaymentResultBodyProps, ActionDispatcher> {

    /* default */ Body(@NonNull final PaymentResultBodyProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    /* default */ boolean hasSomethingToDraw() {
        return hasInstructions() || hasBodyError();
    }

    private boolean hasInstructions() {
        return props.instruction != null;
    }

    private Instructions getInstructionsComponent() {
        final InstructionsProps instructionsProps = new InstructionsProps.Builder()
            .setInstruction(props.instruction)
            .build();
        return new Instructions(instructionsProps, getActions());
    }

    private boolean hasBodyError() {
        final PaymentResultViewModel paymentResultViewModel =
            PaymentResultViewModelFactory.createPaymentResultViewModel(props.paymentResult);
        return paymentResultViewModel.hasBodyError();
    }

    private BodyError getBodyErrorComponent() {
        final BodyErrorProps bodyErrorProps = new BodyErrorProps.Builder()
            .setStatus(props.paymentResult.getPaymentStatus())
            .setStatusDetail(props.paymentResult.getPaymentStatusDetail())
            .setPaymentMethodName(props.paymentResult.getPaymentData().getPaymentMethod().getName())
            .setPaymentAmount(CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currency,
                PaymentDataHelper.getPrettyAmountToPay(props.paymentResult.getPaymentData())))
            .build();
        return new BodyError(bodyErrorProps, getActions());
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        if (hasInstructions()) {
            getInstructionsComponent().render(parent);
        } else if (hasBodyError()) {
            getBodyErrorComponent().render(parent);
        }
        return parent;
    }
}