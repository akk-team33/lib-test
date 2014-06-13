package net.team33.test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("ReturnOfThis")
public class Mapper<K, V> {

    private final Map<K, V> backing;

    private Mapper(final Supplier<Map<K, V>> supplier) {
        backing = supplier.get();
    }

    /**
     * Retrieves a mapper
     *
     * @param supplier Supplies an initial, modifiable, usually empty Map, used as backing for the mapper.
     */
    public static <K, V> Mapper<K, V> mapper(final Supplier<Map<K, V>> supplier) {
        return new Mapper<>(supplier);
    }

    /**
     * Retrieves a mapper, backed by an empty new {@link LinkedHashMap}
     */
    public static <K, V> Mapper<K, V> mapper() {
        return mapper(LinkedHashMap::new);
    }

    /**
     * Builds a new Map as a copy of the backing map in current state.
     *
     * @param function A {@link Function} that specifies the final result.
     */
    public final Map<K, V> build(final Function<Map<K, V>, Map<K, V>> function) {
        return function.apply(backing);
    }

    /**
     * Builds a new modifiable Map as a copy of the backing map in current state.
     * Contains the keys in the same order as the backing map.
     */
    public final Map<K, V> build() {
        return build(LinkedHashMap::new);
    }

    /**
     * Builds a new unmodifiable Map as a copy of the backing map in current state.
     *
     * @param function A {@link Function} that specifies the inner result.
     */
    public final Map<K, V> unmodifiable(final Function<Map<K, V>, Map<K, V>> function) {
        return Collections.unmodifiableMap(function.apply(backing));
    }

    /**
     * Builds a new unmodifiable Map as a copy of the backing map in current state.
     * Contains the keys in the same order as the backing map.
     */
    public final Map<K, V> unmodifiable() {
        return unmodifiable(LinkedHashMap::new);
    }

    public final Mapper<K, V> put(final K key, final V value) {
        backing.put(key, value);
        return this;
    }

    public final Mapper<K, V> putAll(final Map<? extends K, ? extends V> origin) {
        backing.putAll(origin);
        return this;
    }

    public final Mapper<K, V> remove(final Object key) {
        try {
            backing.remove(key);
        } catch (final NullPointerException | ClassCastException ignored) {
            // cannot contain that <key> -> same as simply doesn't contain the key -> nothing to be removed
        }
        return this;
    }
}
