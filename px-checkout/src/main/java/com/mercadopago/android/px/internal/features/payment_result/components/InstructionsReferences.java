package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsReferencesProps;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.InstructionReference;
import java.util.ArrayList;
import java.util.List;

public class InstructionsReferences extends CompactComponent<InstructionsReferencesProps, ActionDispatcher> {

    public InstructionsReferences(@NonNull final InstructionsReferencesProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<InstructionReferenceComponent> getReferenceComponents() {
        final List<InstructionReferenceComponent> componentList = new ArrayList<>();

        for (final InstructionReference reference : props.references) {
            final InstructionReferenceComponent.Props referenceProps = new InstructionReferenceComponent.Props.Builder()
                .setReference(reference)
                .build();

            final InstructionReferenceComponent component =
                new InstructionReferenceComponent(referenceProps, getActions());

            componentList.add(component);
        }

        return componentList;
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View referencesView =
            LayoutInflater.from(context).inflate(R.layout.px_payment_result_instructions_references, parent);
        final ViewGroup referencesViewGroup = referencesView.findViewById(R.id.mpsdkInstructionsReferencesContainer);
        final MPTextView referencesTitle = referencesView.findViewById(R.id.mpsdkInstructionsReferencesTitle);

        ViewUtils.loadOrGone(props.title, referencesTitle);

        /*
         * If we call render with the parent inside a for loop, only the first view is displayed, don't know why,
         * for now we use addView manually to avoid the issue.
         */
        final List<InstructionReferenceComponent> referenceComponentList = getReferenceComponents();
        for (final InstructionReferenceComponent instructionReferenceComponent : referenceComponentList) {
            referencesViewGroup.addView(instructionReferenceComponent.render(parent));
        }

        return referencesView;
    }
}