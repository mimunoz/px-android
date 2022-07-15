package com.mercadopago.android.px.internal.util.textformatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MatcherUtil {

    private MatcherUtil() {
    }

    public static int count(@NonNull final Pattern pattern, @Nullable final CharSequence text) {
        final Matcher matcher = pattern.matcher(text);
        int count = 0;
        int i = 0;
        while (matcher.find(i)) {
            count++;
            i = matcher.start() + 1;
        }
        return count;
    }
}