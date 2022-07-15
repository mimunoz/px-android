package com.mercadopago.android.px.addons.internal;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.FlowBehaviour;

public class FlowDefaultBehaviour implements FlowBehaviour {

    @Override
    public void trackConversion() {
        //Do nothing
    }

    @Override
    public void trackConversion(@NonNull final Result result) {
        //Do nothing
    }
}