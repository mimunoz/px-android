package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsContentProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsSecondaryInfoProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsSubtitleProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import java.util.List;

public class Instructions extends CompactComponent<InstructionsProps, ActionDispatcher> {

    public Instructions(@NonNull final InstructionsProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public InstructionsSubtitle getSubtitleComponent() {
        final InstructionsSubtitleProps subtitleProps = new InstructionsSubtitleProps.Builder()
            .setSubtitle(props.instruction.getSubtitle())
            .build();

        return new InstructionsSubtitle(subtitleProps, getActions());
    }

    public InstructionsContent getContentComponent() {
        final InstructionsContentProps contentProps = new InstructionsContentProps.Builder()
            .setInstruction(props.instruction)
            .build();

        return new InstructionsContent(contentProps, getActions());
    }

    public InstructionsSecondaryInfo getSecondaryInfoComponent() {
        final InstructionsSecondaryInfoProps secondaryInfoProps = new InstructionsSecondaryInfoProps.Builder()
            .setSecondaryInfo(props.instruction.getSecondaryInfo())
            .build();

        return new InstructionsSecondaryInfo(secondaryInfoProps, getActions());
    }

    public boolean hasSubtitle() {
        final String subtitle = props.instruction.getSubtitle();
        return subtitle != null && !subtitle.isEmpty();
    }

    public boolean hasSecondaryInfo() {
        final List<String> secondaryInfoList = props.instruction.getSecondaryInfo();
        return secondaryInfoList != null && !secondaryInfoList.isEmpty();
    }

    public boolean shouldShowEmailInSecondaryInfo() {
        //TODO check this logic
        //return props.processingMode == ProcessingMode.AGGREGATOR;
        return true;
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View instructionsView =
            LayoutInflater.from(context).inflate(R.layout.px_payment_result_instructions, parent);
        final ViewGroup parentViewGroup = instructionsView.findViewById(R.id.mpsdkInstructionsContainer);

        if (hasSubtitle()) {
            getSubtitleComponent().render(parentViewGroup);
        }

        getContentComponent().render(parentViewGroup);

        //TODO backend refactor: secondary info should be an email related component
        if (hasSecondaryInfo() && shouldShowEmailInSecondaryInfo()) {
            getSecondaryInfoComponent().render(parentViewGroup);
        }

        return instructionsView;
    }
}