package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsSubtitleProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;

public class InstructionsSubtitle extends CompactComponent<InstructionsSubtitleProps, ActionDispatcher> {

    /* default */ InstructionsSubtitle(@NonNull final InstructionsSubtitleProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View instructionsView =
            LayoutInflater.from(context).inflate(R.layout.px_payment_result_instructions_subtitle, parent);

        final MPTextView subtitleTextView = instructionsView.findViewById(R.id.msdpkInstructionsSubtitle);
        subtitleTextView.setText(props.subtitle);

        return instructionsView;
    }
}