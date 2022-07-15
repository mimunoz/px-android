package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsActionsProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.InstructionAction;
import java.util.ArrayList;
import java.util.List;

public class InstructionsActions extends CompactComponent<InstructionsActionsProps, ActionDispatcher> {

    public InstructionsActions(@NonNull final InstructionsActionsProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<InstructionsAction> getActionComponents() {
        final List<InstructionsAction> componentList = new ArrayList<>();

        for (final InstructionAction actionInfo : props.instructionActions) {

            if (actionInfo.getTag().equals(InstructionAction.Tags.LINK)) {
                final InstructionsAction.Prop actionProp = new InstructionsAction.Prop.Builder()
                    .setInstructionAction(actionInfo)
                    .build();
                final InstructionsAction component = new InstructionsAction(actionProp, getActions());
                componentList.add(component);
            }
        }

        return componentList;
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View actionsView =
            LayoutInflater.from(context).inflate(R.layout.px_payment_result_instructions_actions, parent);
        final ViewGroup parentViewGroup = actionsView.findViewById(R.id.mpsdkInstructionsActionsContainer);

        final List<InstructionsAction> actionComponentList = getActionComponents();
        for (final InstructionsAction instructionsAction : actionComponentList) {
            instructionsAction.render(parentViewGroup);
        }

        return actionsView;
    }
}