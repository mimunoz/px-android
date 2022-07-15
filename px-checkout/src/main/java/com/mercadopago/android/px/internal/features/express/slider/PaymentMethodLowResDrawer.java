package com.mercadopago.android.px.internal.features.express.slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.internal.features.express.add_new_card.OtherPaymentMethodLowResFragment;
import com.mercadopago.android.px.internal.viewmodel.drawables.AccountMoneyDrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem;
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer;
import com.mercadopago.android.px.internal.viewmodel.drawables.SavedCardDrawableFragmentItem;

public class PaymentMethodLowResDrawer implements PaymentMethodFragmentDrawer {

    @Override
    public Fragment draw(@NonNull final DrawableFragmentItem drawableFragmentItem) {
        throw new IllegalStateException("Unknown type - PaymentMethodFragmentAdapter");
    }

    @Override
    public Fragment draw(@NonNull final OtherPaymentMethodFragmentItem drawableItem) {
        return OtherPaymentMethodLowResFragment.getInstance(drawableItem);
    }

    @Override
    public Fragment draw(@NonNull final SavedCardDrawableFragmentItem drawableItem) {
        return SavedCardLowResFragment.getInstance(drawableItem);
    }

    @Override
    public Fragment draw(@NonNull final AccountMoneyDrawableFragmentItem drawableItem) {
        return AccountMoneyLowResFragment.getInstance(drawableItem);
    }

    @Override
    public Fragment draw(@NonNull final ConsumerCreditsDrawableFragmentItem drawableItem) {
        return ConsumerCreditsLowResFragment.getInstance(drawableItem);
    }
}