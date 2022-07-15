package com.mercadopago.android.px.internal.features.review_and_confirm.components.payment_method;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ReviewAndConfirmViewModel;
import com.mercadopago.android.px.internal.util.MercadoPagoUtil;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.CompactComponent;

/* default */ class MethodOff extends CompactComponent<MethodOff.Props, Void> {

    /* default */ static class Props {
        /* default */ String id;
        /* default */ String title;
        /* default */ Integer time;

        /* default */ Props(final String id, final String title, final Integer time) {
            this.id = id;
            this.title = title;
            this.time = time;
        }

        /* default */ static Props createFrom(final ReviewAndConfirmViewModel props) {
            return new Props(props.paymentMethodId,
                props.paymentMethodName,
                props.accreditationTime);
        }
    }

    /* default */ MethodOff(final Props props) {
        super(props);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final View paymentView = ViewUtils.inflate(parent, R.layout.px_payment_method_off);

        final TextView time = paymentView.findViewById(R.id.time);
        time.setText(MercadoPagoUtil.getAccreditationTimeMessage(time.getContext(), props.time));

        final TextView title = paymentView.findViewById(R.id.title);
        title.setText(props.title);

        final ImageView imageView = paymentView.findViewById(R.id.icon);
        imageView.setImageResource(ResourceUtil.getIconResource(imageView.getContext(), props.id));

        return paymentView;
    }
}