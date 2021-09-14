package com.mercadopago.android.px.internal.features.payment_result.remedies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.express.slider.ConsumerCreditsFragment;
import com.mercadopago.android.px.internal.view.LinkableTextView;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.model.ConsumerCreditsDisplayInfo;

public class ConsumerCreditsMediumResFragment extends ConsumerCreditsFragment {

    @NonNull
    public static Fragment getInstance(@NonNull final ConsumerCreditsDrawableFragmentItem model) {
        final ConsumerCreditsMediumResFragment instance = new ConsumerCreditsMediumResFragment();
        instance.storeModel(model);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_fragment_remedies_consumer_credits_medium_res, container, false);
    }

    @Override
    protected void showDisplayInfo(final View view, @NonNull final ConsumerCreditsDisplayInfo displayInfo) { }

    @Override
    protected void setInstallment(final View view, final int installmentSelected) {
        installment = installmentSelected;
        ((LinkableTextView) view.findViewById(R.id.bottom_text)).updateInstallment(installment);
    }
}