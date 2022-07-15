package com.mercadopago.android.px.addons;

import androidx.annotation.NonNull;

public interface FlowBehaviour {

    enum Result {
        SUCCESS,
        FAILURE,
        PENDING
    }

    /**
     * Called when the flow ended without result.
     */
    void trackConversion();

    /**
     * Called when the flow ended with the specified result.
     *
     * @param result The result
     */
    void trackConversion(@NonNull Result result);
}