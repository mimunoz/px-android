package com.mercadopago.android.px.internal.features.payment_result.props;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.ProcessingMode;

public class InstructionsProps {

    public final Instruction instruction;
    public final ProcessingMode processingMode;

    public InstructionsProps(@NonNull final Builder builder) {
        instruction = builder.instruction;
        processingMode = builder.processingMode;
    }

    public Builder toBuilder() {
        return new Builder()
            .setInstruction(instruction)
            .setProcessingMode(processingMode);
    }

    public static class Builder {

        public Instruction instruction;
        public ProcessingMode processingMode;

        public Builder setInstruction(@NonNull final Instruction instruction) {
            this.instruction = instruction;
            return this;
        }

        public Builder setProcessingMode(final ProcessingMode processingMode) {
            this.processingMode = processingMode;
            return this;
        }

        public InstructionsProps build() {
            return new InstructionsProps(this);
        }
    }
}
