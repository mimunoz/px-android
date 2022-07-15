package com.mercadopago.android.px.internal.features.payment_result.props;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Instruction;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsContentProps {

    public final Instruction instruction;

    public InstructionsContentProps(@NonNull final Instruction instruction) {
        this.instruction = instruction;
    }

    public InstructionsContentProps(@NonNull final Builder builder) {
        instruction = builder.instruction;
    }

    public Builder toBuilder() {
        return new Builder()
            .setInstruction(instruction);
    }

    public static class Builder {

        public Instruction instruction;

        public Builder setInstruction(@NonNull Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public InstructionsContentProps build() {
            return new InstructionsContentProps(this);
        }
    }
}
