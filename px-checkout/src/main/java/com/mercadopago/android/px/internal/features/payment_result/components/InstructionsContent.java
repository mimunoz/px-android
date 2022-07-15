package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionInteractionsProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsActionsProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsContentProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsInfoProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsReferencesProps;
import com.mercadopago.android.px.internal.features.payment_result.props.InstructionsTertiaryInfoProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.InstructionAction;
import com.mercadopago.android.px.model.InstructionReference;
import com.mercadopago.android.px.model.Interaction;
import java.util.ArrayList;
import java.util.List;

public class InstructionsContent extends CompactComponent<InstructionsContentProps, ActionDispatcher> {

    public InstructionsContent(@NonNull final InstructionsContentProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public boolean hasInfo() {
        final List<String> info = props.instruction.getInfo();
        return info != null && !info.isEmpty();
    }

    public boolean hasReferences() {
        final List<InstructionReference> references = props.instruction.getReferences();
        return references != null && !references.isEmpty();
    }

    public boolean hasAccreditationTime() {
        final String accreditationMessage = props.instruction.getAcreditationMessage();
        final boolean hasMessage = accreditationMessage != null && !accreditationMessage.isEmpty();
        final List<String> accreditationComments = props.instruction.getAccreditationComments();
        final boolean hasComments = accreditationComments != null && !accreditationComments.isEmpty();
        return hasMessage || hasComments;
    }

    private boolean hasInstructionInteractions() {
        final List<Interaction> interactions = props.instruction.getInteractions();
        return interactions != null && !interactions.isEmpty();
    }

    public boolean hasActions() {
        final List<InstructionAction> instructionActionList = props.instruction.getActions();
        if (instructionActionList != null && !instructionActionList.isEmpty()) {
            for (final InstructionAction actionInfo : instructionActionList) {
                if (actionInfo.getTag().equals(InstructionAction.Tags.LINK)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTertiaryInfo() {
        final List<String> tertiaryInfoList = props.instruction.getTertiaryInfo();
        return tertiaryInfoList != null && !tertiaryInfoList.isEmpty();
    }

    private boolean needsBottomMargin() {
        return hasInfo() || hasReferences() || hasAccreditationTime();
    }

    public InstructionsInfo getInfoComponent() {
        final List<String> content = new ArrayList<>();
        final List<String> info = props.instruction.getInfo();

        String title = "";
        boolean hasTitle = false;
        if (info.size() == 1 || (info.size() > 1 && info.get(1).isEmpty())) {
            title = info.get(0);
            hasTitle = true;
        }

        boolean firstSpaceFound = false;
        boolean secondSpaceFound = false;
        boolean hasBottomDivider = false;
        for (final String text : info) {
            if (text.isEmpty()) {
                if (firstSpaceFound) {
                    secondSpaceFound = true;
                } else {
                    firstSpaceFound = true;
                }
            } else {
                if (!hasTitle || (firstSpaceFound && !secondSpaceFound)) {
                    content.add(text);
                } else if (firstSpaceFound && secondSpaceFound) {
                    hasBottomDivider = true;
                }
            }
        }

        final InstructionsInfoProps infoProps = new InstructionsInfoProps.Builder()
            .setInfoTitle(title)
            .setInfoContent(content)
            .setBottomDivider(hasBottomDivider)
            .build();

        return new InstructionsInfo(infoProps, getActions());
    }

    public InstructionsReferences getReferencesComponent() {
        final List<String> info = props.instruction.getInfo();
        int spacesFound = 0;
        String title = "";
        for (final String text : info) {
            if (text.isEmpty()) {
                spacesFound++;
            } else if (spacesFound == 2) {
                title = text;
                break;
            }
        }

        final InstructionsReferencesProps referencesProps = new InstructionsReferencesProps.Builder()
            .setTitle(title)
            .setReferences(props.instruction.getReferences())
            .build();

        return new InstructionsReferences(referencesProps, getActions());
    }

    public InstructionsTertiaryInfo getTertiaryInfoComponent() {
        final InstructionsTertiaryInfoProps tertiaryInfoProps = new InstructionsTertiaryInfoProps.Builder()
            .setTertiaryInfo(props.instruction.getTertiaryInfo())
            .build();

        return new InstructionsTertiaryInfo(tertiaryInfoProps, getActions());
    }

    public AccreditationTime getAccreditationTimeComponent() {
        final AccreditationTime.Props accreditationTimeProps = new AccreditationTime.Props.Builder()
            .setAccreditationMessage(props.instruction.getAcreditationMessage())
            .setAccreditationComments(props.instruction.getAccreditationComments())
            .build();

        return new AccreditationTime(accreditationTimeProps, getActions());
    }

    public InstructionsActions getActionsComponent() {
        final InstructionsActionsProps instructionsActionsProps = new InstructionsActionsProps.Builder()
            .setInstructionsActions(props.instruction.getActions())
            .build();

        return new InstructionsActions(instructionsActionsProps, getActions());
    }

    private InstructionInteractions getInteractionsComponent() {
        final InstructionInteractionsProps instructionInteractionsProps = new InstructionInteractionsProps.Builder()
            .setInstructionInteractions(props.instruction.getInteractions())
            .build();

        return new InstructionInteractions(instructionInteractionsProps, getActions());
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View instructionsView = LayoutInflater
            .from(context).inflate(R.layout.px_payment_result_instructions_content, parent);
        final ViewGroup parentViewGroup = instructionsView.findViewById(R.id.mpsdkInstructionsContentContainer);
        final View bottomMarginView = instructionsView.findViewById(R.id.mpsdkContentBottomMargin);

        if (needsBottomMargin()) {
            bottomMarginView.setVisibility(View.VISIBLE);
        } else {
            bottomMarginView.setVisibility(View.GONE);
        }

        if (hasInfo()) {
            getInfoComponent().render(parentViewGroup);
        }
        if(hasInstructionInteractions()){
            getInteractionsComponent().render(parentViewGroup);
        }
        if (hasReferences()) {
            getReferencesComponent().render(parentViewGroup);
        }
        if (hasTertiaryInfo()) {
            getTertiaryInfoComponent().render(parentViewGroup);
        }
        if (hasAccreditationTime()) {
            getAccreditationTimeComponent().render(parentViewGroup);
        }
        if (hasActions()) {
            getActionsComponent().render(parentViewGroup);
        }

        return instructionsView;
    }
}