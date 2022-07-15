package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsSecondaryInfoProps;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;
import java.util.List;

public class InstructionsSecondaryInfo extends CompactComponent<InstructionsSecondaryInfoProps, ActionDispatcher> {

    /* default */ InstructionsSecondaryInfo(@NonNull final InstructionsSecondaryInfoProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View secondaryInfoView =
            LayoutInflater.from(context).inflate(R.layout.px_payment_result_instructions_secondary_info, parent);
        final MPTextView secondaryInfoTextView = secondaryInfoView.findViewById(R.id.msdpkSecondaryInfo);

        ViewUtils.loadOrGone(getSecondaryInfoText(props.secondaryInfo), secondaryInfoTextView);
        return secondaryInfoView;
    }

    private String getSecondaryInfoText(final List<String> secondaryInfo) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < secondaryInfo.size(); i++) {
            stringBuilder.append(secondaryInfo.get(i));
            if (i != secondaryInfo.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}