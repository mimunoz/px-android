package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Currency;

class StyleSmallDecimal extends Style {

    StyleSmallDecimal(@NonNull final AmountFormatter amountFormatter) {
        super(amountFormatter);
    }

    @Override
    public Spannable apply(final int holder, final Context context) {
        final Spannable spanned = apply(null);
        final Currency currency = amountFormatter.currencyFormatter.currency;
        final Character decimalSeparator = currency.getDecimalSeparator();
        final SpannableString spannableString = new SpannableString(TextUtil.format(context, holder, spanned));
        final String totalText = spannableString.toString();
        return makeSmallAfterSeparator(decimalSeparator, totalText);
    }

    @Override
    public Spannable toSpannable() {
        return apply(null);
    }

    private Spannable makeSmallAfterSeparator(final Character decimalSeparator, final String totalText) {
        if (totalText.contains(decimalSeparator.toString())) {
            final String[] splitted = totalText.split(decimalSeparator.toString());
            final String concat = splitted[0].concat(splitted[1]);
            final SpannableStringBuilder spannable = makeSmall(splitted[0], concat);
            return new SpannableString(spannable);
        }
        return new SpannableString(totalText);
    }

    @NonNull
    private SpannableStringBuilder makeSmall(final String big, final String all) {
        final SpannableStringBuilder spannable = new SpannableStringBuilder(all);
        spannable.setSpan(new RelativeSizeSpan(0.5f), big.length(), all.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new SuperscriptSpanAdjuster(0.7f), big.length(), all.length(),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public Spannable apply(@Nullable final CharSequence charSequence) {
        final Currency currency = amountFormatter.currencyFormatter.currency;
        final Character decimalSeparator = currency.getDecimalSeparator();
        final Spannable localizedAmount = amountFormatter.apply(charSequence);
        final String totalText = localizedAmount.toString();
        return makeSmallAfterSeparator(decimalSeparator, totalText);
    }
}
