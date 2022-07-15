package com.mercadopago.android.px.internal.features.express.add_new_card;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem;
import com.mercadopago.android.px.model.internal.Text;

public class OtherPaymentMethodLowResFragment extends OtherPaymentMethodFragment {

    @NonNull
    public static Fragment getInstance(@NonNull final OtherPaymentMethodFragmentItem model) {
        final OtherPaymentMethodLowResFragment instance = new OtherPaymentMethodLowResFragment();
        instance.storeModel(model);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        final boolean smallMode = model.getNewCardMetadata() != null && model.getOfflineMethodsMetadata() != null;

        return smallMode ? inflater.inflate(R.layout.px_fragment_other_payment_method_small_low_res, container, false) :
            inflater.inflate(R.layout.px_fragment_other_payment_method_large_low_res, container, false);
    }

    @Override
    protected void loadSecondaryMessageView(@NonNull final View view, @Nullable final Text secondaryMessage) { }
}