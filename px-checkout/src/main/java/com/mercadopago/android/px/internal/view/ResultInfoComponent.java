package com.mercadopago.android.px.internal.view;

import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.display_info.ResultInfo;

public class ResultInfoComponent extends CompactComponent<ResultInfo, Void> {

    /* default */ ResultInfoComponent(@NonNull final ResultInfo props) {
        super(props);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final View view = ViewUtils.inflate(parent, R.layout.px_view_digital_currency_labels);
        final MPTextView title = view.findViewById(R.id.title);
        final MPTextView subtitle = view.findViewById(R.id.subtitle);
        ViewUtils.loadOrGone(props.getTitle(), title);
        ViewUtils.loadOrGone(props.getSubtitle(), subtitle);
        return view;
    }
}