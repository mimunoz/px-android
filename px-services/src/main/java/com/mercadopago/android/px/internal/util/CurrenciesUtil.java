package com.mercadopago.android.px.internal.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.RelativeSizeSpan;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.textformatter.SuperscriptSpanAdjuster;
import com.mercadopago.android.px.model.Currency;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public final class CurrenciesUtil {

    private static final String CURRENCY_ARGENTINA = "ARS";
    private static final String CURRENCY_BRAZIL = "BRL";
    private static final String CURRENCY_CHILE = "CLP";
    private static final String CURRENCY_COLOMBIA = "COP";
    private static final String CURRENCY_MEXICO = "MXN";
    private static final String CURRENCY_VENEZUELA = "VES";
    private static final String CURRENCY_USA = "USD";
    private static final String CURRENCY_PERU = "PEN";
    private static final String CURRENCY_URUGUAY = "UYU";
    private static final String ZERO_DECIMAL = "00";

    private static Map<String, Currency> currenciesList = new HashMap<String, Currency>() {{
        put(CURRENCY_ARGENTINA,
            new Currency(CURRENCY_ARGENTINA, "Peso argentino", "$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_BRAZIL, new Currency(CURRENCY_BRAZIL, "Real", "R$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_CHILE, new Currency(CURRENCY_CHILE, "Peso chileno", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_COLOMBIA,
            new Currency(CURRENCY_COLOMBIA, "Peso colombiano", "$", 0, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_MEXICO, new Currency(CURRENCY_MEXICO, "Peso mexicano", "$", 2, ".".charAt(0), ",".charAt(0)));
        put(CURRENCY_VENEZUELA,
            new Currency(CURRENCY_VENEZUELA, "Bolívares Soberanos", "BsS", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_USA, new Currency(CURRENCY_USA, "Dolar americano", "US$", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_PERU, new Currency(CURRENCY_PERU, "Soles", "S/.", 2, ",".charAt(0), ".".charAt(0)));
        put(CURRENCY_URUGUAY, new Currency(CURRENCY_URUGUAY, "Peso Uruguayo", "$", 2, ",".charAt(0), ".".charAt(0)));
    }};

    private CurrenciesUtil() {
    }

    public static String getLocalizedAmountNoDecimals(final BigDecimal truncated, final Currency currency) {
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(currency.getDecimalSeparator());
        dfs.setGroupingSeparator(currency.getThousandsSeparator());
        final DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(dfs);
        df.setMinimumFractionDigits(0);
        df.setMaximumFractionDigits(0);
        return df.format(truncated);
    }

    public static String getLocalizedAmount(@NonNull final BigDecimal amount, final Currency currency) {
        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(currency.getDecimalSeparator());
        dfs.setGroupingSeparator(currency.getThousandsSeparator());
        final DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(dfs);
        df.setMinimumFractionDigits(currency.getDecimalPlaces());
        df.setMaximumFractionDigits(currency.getDecimalPlaces());
        return df.format(amount);
    }

    public static Spanned getSpannedAmountWithCurrencySymbol(final BigDecimal amount, final Currency currency) {
        return CurrenciesUtil.getSpannedString(amount, currency, false, true);
    }

    private static String getDecimals(final Currency currency, final BigDecimal amount) {
        final String localizedAmount = getLocalizedAmount(amount, currency);
        final int decimalDivisionIndex = localizedAmount.indexOf(currency.getDecimalSeparator());
        String decimals = null;
        if (decimalDivisionIndex != -1) {
            decimals = localizedAmount.substring(decimalDivisionIndex + 1);
        }
        return decimals;
    }

    private static Spanned getSpannedString(final BigDecimal amount, final Currency currency, final boolean symbolUp,
        final boolean decimalsUp) {
        String localizedAmount = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(currency, amount);
        SpannableStringBuilder spannableAmount = new SpannableStringBuilder(localizedAmount);
        if (decimalsUp && !CurrenciesUtil.hasZeroDecimals(currency, amount)) {
            final int fromDecimals = localizedAmount.indexOf(currency.getDecimalSeparator()) + 1;
            localizedAmount =
                localizedAmount.replace(String.valueOf(currency.getDecimalSeparator()), " ");
            spannableAmount = new SpannableStringBuilder(localizedAmount);
            decimalsUp(currency, amount, spannableAmount, fromDecimals);
        }

        if (symbolUp) {
            symbolUp(currency, localizedAmount, spannableAmount);
        }

        return new SpannedString(spannableAmount);
    }

    /**
     * @deprecated only exists because moneyIn depends on it. Will be eliminated in next releases.
     */
    @Deprecated
    public static String getLocalizedAmountWithoutZeroDecimals(@NonNull final String currencyId,
        @NonNull final BigDecimal amount) {
        //noinspection ConstantConditions
        return getLocalizedAmountWithoutZeroDecimals(currenciesList.get(currencyId), amount);
    }

    public static String getLocalizedAmountWithoutZeroDecimals(@NonNull final Currency currency,
        @NonNull final BigDecimal amount) {
        String localized = getLocalizedAmountWithCurrencySymbol(amount, currency);
        if (hasZeroDecimals(currency, amount)) {
            final Character decimalSeparator = currency.getDecimalSeparator();
            final int decimalIndex = localized.indexOf(decimalSeparator);
            if (decimalIndex >= 0) {
                localized = localized.substring(0, decimalIndex);
            }
        }
        return localized;
    }

    private static String getLocalizedAmountWithCurrencySymbol(final BigDecimal amount, final Currency currency) {
        // Get currency configuration
        final String formattedAmount = getLocalizedAmount(amount, currency);
        // return formatted string
        return currency.getSymbol() + " " + formattedAmount;
    }

    private static boolean hasZeroDecimals(final Currency currency, final BigDecimal amount) {
        final String decimals = getDecimals(currency, amount);
        return ZERO_DECIMAL.equals(decimals) || TextUtil.isEmpty(decimals);
    }

    private static void symbolUp(@NonNull final Currency currency, final String localizedAmount,
        final Spannable spannableAmount) {
        final int fromSymbolPosition = localizedAmount.indexOf(currency.getSymbol());
        final int toSymbolPosition = fromSymbolPosition + currency.getSymbol().length();
        spannableAmount.setSpan(new RelativeSizeSpan(0.5f), fromSymbolPosition, toSymbolPosition,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SuperscriptSpanAdjuster(0.65f), fromSymbolPosition, toSymbolPosition,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void decimalsUp(final Currency currency, final BigDecimal amount,
        final Spannable spannableAmount, final int fromDecimals) {
        final int toDecimals = fromDecimals + CurrenciesUtil.getDecimals(currency, amount).length();
        spannableAmount
            .setSpan(new RelativeSizeSpan(0.5f), fromDecimals, toDecimals, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new SuperscriptSpanAdjuster(0.7f), fromDecimals, toDecimals,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
