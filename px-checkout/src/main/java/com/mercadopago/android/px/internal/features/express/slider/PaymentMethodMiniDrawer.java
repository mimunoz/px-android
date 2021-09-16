package com.mercadopago.android.px.internal.features.express.slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.internal.features.express.add_new_card.OtherPaymentMethodLowResFragment;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer;

public class PaymentMethodMiniDrawer implements PaymentMethodFragmentDrawer {

    @Override
    public Fragment draw(@NonNull final DrawableFragmentItem drawableFragmentItem) {
        return CardMiniFragment.getInstance(drawableFragmentItem);
    }

    @Override
    public Fragment draw(@NonNull final OtherPaymentMethodFragmentItem drawableItem) {
        return OtherPaymentMethodLowResFragment.getInstance(drawableItem);
    }

    @Override
    public Fragment draw(@NonNull final ConsumerCreditsDrawableFragmentItem drawableItem) {
        return ConsumerCreditsMiniFragment.getInstance(drawableItem);
    }
}