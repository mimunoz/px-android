package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;

import static com.mercadopago.android.px.internal.util.TextUtil.isNotEmpty;

public class TotalLocalized implements ILocalizedCharSequence {

    @Override
    public CharSequence get(@NonNull final Context context) {
        final String descriptionText =
            Session.getInstance().getConfigurationModule().getPaymentSettings().getAdvancedConfiguration()
                .getCustomStringConfiguration().getTotalDescriptionText();

        return isNotEmpty(descriptionText) ? descriptionText : context.getString(R.string.px_total_to_pay);
    }
}