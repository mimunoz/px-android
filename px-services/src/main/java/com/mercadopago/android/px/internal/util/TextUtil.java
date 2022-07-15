package com.mercadopago.android.px.internal.util;

import android.content.Context;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import com.mercadopago.android.px.internal.util.textformatter.MatcherUtil;
import java.util.regex.Pattern;

public final class TextUtil {

    public static final String EMPTY = "";
    public static final String NL = "\n";
    public static final String DOT = ".";
    public static final CharSequence SPACE = " ";
    public static final CharSequence UNDERSCORE = "_";
    public static final String CSV_DELIMITER = ",";

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");
    private static final Pattern PLACE_HOLDER_PATTERN = Pattern.compile("\\{[0-9]*\\}");

    private TextUtil() {
        throw new AssertionError("Util classes shouldn't be instantiated.");
    }

    public static boolean isEmpty(@Nullable final CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean isNotEmpty(@Nullable final CharSequence text) {
        return !isEmpty(text);
    }

    public static boolean isDigitsOnly(@Nullable final CharSequence text) {
        return text != null && DIGIT_PATTERN.matcher(text).matches();
    }

    public static <T extends CharSequence> T ifNotEmptyOrElse(@Nullable final T text, @NonNull final T fallback) {
        return isNotEmpty(text) ? text : fallback;
    }

    @NonNull
    public static String join(@Nullable final Iterable<String> values) {
        return values != null ? TextUtils.join(CSV_DELIMITER, values) : "";
    }

    public static String format(@NonNull final Context context, @StringRes final int resId,
        @NonNull final CharSequence... args) {
        return resId == 0 ? EMPTY : format(context.getString(resId), args);
    }

    public static String format(@NonNull final Context context, @PluralsRes final int resId, final int quantity,
        @NonNull final String... args) {
        return resId == 0 ? EMPTY : format(context.getResources().getQuantityString(resId, quantity), args);
    }

    public static String format(@NonNull final String text, @NonNull final CharSequence... args) {
        if (args.length != MatcherUtil.count(PLACE_HOLDER_PATTERN, text)) {
            throw new IllegalStateException("There is a different amount of placeholder than arguments");
        }
        String result = text;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + i + "}", args[i]);
        }
        return result;
    }
}