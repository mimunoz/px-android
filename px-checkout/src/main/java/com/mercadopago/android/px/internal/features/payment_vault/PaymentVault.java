package com.mercadopago.android.px.internal.features.payment_vault;

import android.content.Intent;
import androidx.annotation.Nullable;

public interface PaymentVault {

    interface Actions {
        void trackOnBackPressed();

        void onActivityResultNotOk(@Nullable final Intent data);
    }
}