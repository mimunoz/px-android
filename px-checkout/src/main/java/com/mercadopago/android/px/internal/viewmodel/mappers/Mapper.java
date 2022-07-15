package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public abstract class Mapper<T, V> {

    public abstract V map(@NonNull T val);

    public List<V> map(@NonNull final Iterable<T> val) {
        final List<V> returned = new ArrayList<>();
        for (final T value : val) {
            returned.add(map(value));
        }
        return returned;
    }
}