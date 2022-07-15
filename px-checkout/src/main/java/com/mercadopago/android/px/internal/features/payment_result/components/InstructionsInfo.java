package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsInfoProps;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;
import java.util.List;

public class InstructionsInfo extends CompactComponent<InstructionsInfoProps, ActionDispatcher> {

    /* default */ InstructionsInfo(@NonNull final InstructionsInfoProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View infoView = LayoutInflater.from(context).inflate(R.layout.px_payment_result_instructions_info, parent);
        final MPTextView infoTitle = infoView.findViewById(R.id.mpsdkInstructionsInfoTitle);
        final MPTextView infoContent = infoView.findViewById(R.id.mpsdkInstructionsInfoContent);
        final View bottomDivider = infoView.findViewById(R.id.mpsdkInstructionsInfoDividerBottom);

        ViewUtils.loadOrGone(props.infoTitle, infoTitle);

        if (props.infoContent == null || props.infoContent.isEmpty()) {
            infoContent.setVisibility(View.GONE);
        } else {
            infoContent.setText(getInfoText(props.infoContent));
            infoContent.setVisibility(View.VISIBLE);
        }

        if (props.bottomDivider) {
            bottomDivider.setVisibility(View.VISIBLE);
        } else {
            bottomDivider.setVisibility(View.GONE);
        }

        return infoView;
    }

    private String getInfoText(final List<String> info) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < info.size(); i++) {
            stringBuilder.append(info.get(i));
            if (i != info.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}