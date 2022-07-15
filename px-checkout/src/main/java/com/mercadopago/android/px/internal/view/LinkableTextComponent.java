package com.mercadopago.android.px.internal.view;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.components.LineSeparator;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.display_info.LinkableText;

public class LinkableTextComponent extends CompactComponent<LinkableText, Void> {

    public LinkableTextComponent(@NonNull final LinkableText props) {
        super(props);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final LinearLayout linearContainer = ViewUtils.createLinearContainer(context);
        final View view = ViewUtils.inflate(parent, R.layout.px_view_digital_currency_terms_and_conditions);
        ((LinkableTextView) view.findViewById(R.id.message)).updateModel(props);
        final LineSeparator lineSeparator = new LineSeparator(new LineSeparator.Props(R.color.px_med_light_gray));
        linearContainer.addView(lineSeparator.render(linearContainer));
        linearContainer.addView(view);
        return linearContainer;
    }
}