package com.mercadopago.android.px.internal.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Collection;

public final class ListUtil {

    public static final Matcher<String> CONTAIN_IGNORE_CASE = String::equalsIgnoreCase;

    private ListUtil() {
        throw new AssertionError("Util classes shouldn't be instantiated.");
    }

    public static boolean isEmpty(@Nullable final Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable final Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> boolean contains(@Nullable final Collection<T> list, @Nullable final T value,
        @NonNull final Matcher<T> matcher) {
        if (value == null || isEmpty(list)) {
            return false;
        }
        boolean contains = false;
        for (final T element : list) {
            contains |= matcher.apply(element, value);
        }
        return contains;
    }

    public interface Matcher<T> {
        boolean apply(@NonNull T object1, @NonNull T object2);
    }
}