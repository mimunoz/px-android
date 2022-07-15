package com.mercadopago.android.px.internal.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;

public class DiscountAmountLocalized implements ILocalizedCharSequence {

    private final BigDecimal amount;
    private final Currency currency;

    public DiscountAmountLocalized(@NonNull final BigDecimal amount, @NonNull final Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public CharSequence get(@NonNull final Context context) {
        final Editable spannableStringBuilder = new SpannableStringBuilder();
        final Spannable spannable =
            TextFormatter.withCurrency(currency)
                .withSpace()
                .amount(amount)
                .normalDecimals()
                .toSpannable();
        spannableStringBuilder.append(TextUtil.format(context, R.string.px_prefix_discount_amount, spannable));
        return spannableStringBuilder;
    }
}