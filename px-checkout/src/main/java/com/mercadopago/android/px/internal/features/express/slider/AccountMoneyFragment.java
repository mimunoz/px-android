package com.mercadopago.android.px.internal.features.express.slider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.DisableConfiguration;
import com.mercadopago.android.px.internal.viewmodel.drawables.AccountMoneyDrawableFragmentItem;

public class AccountMoneyFragment extends PaymentMethodFragment<AccountMoneyDrawableFragmentItem> {

    @NonNull
    public static Fragment getInstance(@NonNull final AccountMoneyDrawableFragmentItem model) {
        final AccountMoneyFragment instance = new AccountMoneyFragment();
        instance.storeModel(model);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_fragment_account_money, container, false);
    }

    @Override
    public void disable() {
        super.disable();
        final View view = getView();
        final DisableConfiguration disableConfiguration = new DisableConfiguration(getContext());
        if (view != null) {
            final ViewGroup card = view.findViewById(R.id.payment_method);
            final ImageView background = view.findViewById(R.id.background);

            ViewUtils.grayScaleViewGroup(card);
            background.clearColorFilter();
            background.setImageResource(0);
            background.setBackgroundColor(disableConfiguration.getBackgroundColor());
        }
    }

    @Override
    protected String getAccessibilityContentDescription() {
        return model.getDescription();
    }
}