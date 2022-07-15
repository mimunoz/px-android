package com.mercadopago.android.px.internal.features.review_and_confirm.components.payment_method;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ReviewAndConfirmViewModel;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.Button;
import com.mercadopago.android.px.internal.view.ButtonLink;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentTypes;

public class PaymentMethodComponent extends CompactComponent<ReviewAndConfirmViewModel, PaymentMethodComponent.Actions> {

    public interface Actions {
        void onPaymentMethodChangeClicked();
    }

    public PaymentMethodComponent(final ReviewAndConfirmViewModel props, final Actions actions) {
        super(props, actions);
    }

    @VisibleForTesting()
    CompactComponent resolveComponent() {
        // TODO we could infer what to render by props' fields instead of payment type and have just one renderer
        if (PaymentTypes.isCardPaymentType(props.getPaymentType())) {
            return new MethodCard(MethodCard.Props.createFrom(props));
        } else if (PaymentTypes.isAccountMoney(props.getPaymentType()) ||
            PaymentMethods.CONSUMER_CREDITS.equals(props.paymentMethodId)) {
            return new MethodAccountMoney(MethodAccountMoney.Props.createFrom(props));
        } else {
            return new MethodOff(MethodOff.Props.createFrom(props));
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final ViewGroup paymentMethodView = (ViewGroup) resolveComponent().render(parent);

        if (shouldShowPaymentMethodButton()) {
            final String changeLabel = parent.getContext().getString(R.string.px_change_payment);
            final ButtonLink buttonLink = new ButtonLink(new Button.Props(changeLabel, null), action -> {
                if (getActions() != null) {
                    getActions().onPaymentMethodChangeClicked();
                }
            });

            ViewUtils.compose(paymentMethodView, buttonLink.render(paymentMethodView));
        }

        return paymentMethodView;
    }

    @VisibleForTesting
    boolean shouldShowPaymentMethodButton() {
        return props.hasMoreThanOnePaymentMethod() || PaymentTypes.isCardPaymentType(props.getPaymentType());
    }
}