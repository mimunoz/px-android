package com.mercadopago.android.px.internal.features.payment_result.remedies;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.internal.features.express.add_new_card.OtherPaymentMethodLowResFragment;
import com.mercadopago.android.px.internal.features.express.slider.CardLowResFragment;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer;

public class PaymentMethodRemediesLargeResDrawer implements PaymentMethodFragmentDrawer {

    @Override
    public Fragment draw(@NonNull final DrawableFragmentItem drawableFragmentItem) {
        return null;
    }

    @Override
    public Fragment draw(@NonNull final OtherPaymentMethodFragmentItem drawableItem) {
        return null;
    }

    @Override
    public Fragment draw(@NonNull final ConsumerCreditsDrawableFragmentItem drawableItem) {
        return ConsumerCreditsLargeResFragment.getInstance(drawableItem);
    }
}