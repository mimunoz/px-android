package com.mercadopago.android.px.internal.util.textformatter;

import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.Currency;

public class TextFormatter {

    @NonNull private final TextView textView;
    @NonNull private final Style style;
    @StringRes private int holder;

    TextFormatter(@NonNull final TextView textView, @NonNull final Style style) {
        this.textView = textView;
        this.style = style;
        holder = R.string.px_string_holder;
        setFormatted();
    }

    public TextFormatter strike() {
        textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        return this;
    }

    public TextFormatter normal() {
        textView.setPaintFlags(0);
        return this;
    }

    public static CurrencyFormatter withCurrency(@NonNull final Currency currency) {
        return new CurrencyFormatter(currency);
    }

    public TextFormatter holder(@StringRes final int holder) {
        this.holder = holder;
        setFormatted();
        return this;
    }

    private void setFormatted() {
        textView.setText(style.apply(holder, textView.getContext()));
    }

    public TextFormatter visible(final boolean visible) {
        textView.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public Spannable toSpannable() {
        return style.apply(null);
    }
}