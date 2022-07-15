package com.mercadopago.android.px.internal.viewmodel.mappers;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CacheableMapper<T, V, K> extends Mapper<T, V> {

    @Override
    public List<V> map(@NonNull final Iterable<T> val) {
        final Map<K, V> cache = new HashMap<>();
        final List<V> returned = new ArrayList<>();
        for (final T value : val) {
            returned.add(mapWithCache(cache, value));
        }
        return returned;
    }

    private V mapWithCache(@NonNull final Map<K, V> cache, @NonNull final T val) {
        final K key = getKey(val);
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            final V mappedVal = map(val);
            cache.put(key, mappedVal);
            return mappedVal;
        }
    }

    protected abstract K getKey(@NonNull final T val);
}