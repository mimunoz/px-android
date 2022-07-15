package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionInteractionsProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.Interaction;
import java.util.ArrayList;
import java.util.List;

public class InstructionInteractions extends CompactComponent<InstructionInteractionsProps, ActionDispatcher> {

    /* default */ InstructionInteractions(@NonNull final InstructionInteractionsProps instructionInteractionsProps,
        @NonNull final ActionDispatcher dispatcher) {
        super(instructionInteractionsProps, dispatcher);
    }

    private List<InstructionInteractionComponent> getInteractionComponents() {
        final List<InstructionInteractionComponent> componentList = new ArrayList<>();

        for (final Interaction interaction : props.interactions) {
            final InstructionInteractionComponent.Props interactionProps =
                new InstructionInteractionComponent.Props.Builder()
                    .setInteraction(interaction)
                    .build();

            final InstructionInteractionComponent component =
                new InstructionInteractionComponent(interactionProps, getActions());

            componentList.add(component);
        }

        return componentList;
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final LinearLayout instructionsView = new LinearLayout(context);
        instructionsView.setOrientation(LinearLayout.VERTICAL);
        instructionsView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        final int sideDimensionPadding = (int) context.getResources().getDimension(R.dimen.px_l_margin);
        final int bottomDimensionPadding = (int) context.getResources().getDimension(R.dimen.px_xxs_margin);
        instructionsView.setPadding(sideDimensionPadding, 0, sideDimensionPadding, bottomDimensionPadding);

        final List<InstructionInteractionComponent> interactionComponentList = getInteractionComponents();
        for (final InstructionInteractionComponent instructionInteractionComponent : interactionComponentList) {
            instructionsView.addView(instructionInteractionComponent.render(parent));
        }

        parent.addView(instructionsView);
        return parent;
    }
}