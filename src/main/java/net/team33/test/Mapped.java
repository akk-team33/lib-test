package net.team33.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

@SuppressWarnings("UnusedDeclaration")
public class Mapped<K extends Mapped.Key> {

    private static final String VALUE_MUST_NOT_BE_NULL
            = "<value> must not be <null>";
    private static final String ORIGIN_DOES_NOT_MATCH_ITS_REQUIREMENTS
            = "At least one entry of <origin> doesn't match it's requirements: ";

    private final Map<K, Object> backing;

    public Mapped(final Set<K> keySet, final Map<? extends K, ?> origin) throws IllegalArgumentException {
        this(keySet, origin, false);
    }

    public Mapped(final Collection<? extends K> keys, final Map<? extends K, ?> origin, final boolean ignoreOverhead)
            throws IllegalArgumentException {

        this.backing = unmodifiableMap(copy(toSet(keys), origin, ignoreOverhead));
    }

    public static <K extends Key> Builder<K, Mapped<K>> builder(final Collection<? extends K> keys) {
        return builder(keys, emptyMap());
    }

    public static <K extends Key> Builder<K, Mapped<K>> builder(
            final Collection<? extends K> keys, final Map<? extends K, ?> origin) throws IllegalArgumentException {

        return builder(keys, origin, false);
    }

    public static <K extends Key> Builder<K, Mapped<K>> builder(
            final Collection<? extends K> keys, final Map<? extends K, ?> origin, final boolean ignoreOverhead)
            throws IllegalArgumentException {

        return new Builder<>(keys, origin, template -> new Mapped<>(keys, template, false), ignoreOverhead);
    }

    private static <K extends Key> Map<K, Object> copy(
            final Set<K> keySet, final Map<? extends K, ?> origin, final boolean ignoreOverhead)
            throws IllegalArgumentException {

        if (ignoreOverhead || keySet.containsAll(origin.keySet())) {
            try {
                final Map<K, Object> result = new HashMap<>(keySet.size());
                for (final K key : keySet) {
                    final Object value = origin.containsKey(key) ? origin.get(key) : key.getDefault();
                    result.put(key, cast(key, value));
                }
                return result;

            } catch (final ClassCastException | NullPointerException caught) {
                throw new IllegalArgumentException(
                        ORIGIN_DOES_NOT_MATCH_ITS_REQUIREMENTS + origin, caught);
            }

        } else {
            final Set<K> overhead = new HashSet<>(origin.keySet());
            overhead.removeAll(keySet);
            throw new IllegalArgumentException("<origin> contains invalid keys: " + overhead);
        }
    }

    private static <K extends Key> Object cast(final K key, final Object value)
            throws NullPointerException, ClassCastException {

        if (null != value || key.isNullable()) {
            return key.getValueClass().cast(value);
        } else {
            throw new NullPointerException(VALUE_MUST_NOT_BE_NULL);
        }
    }

    @SuppressWarnings("unchecked")
    private static <K> Set<K> toSet(final Collection<? extends K> keys) {
        return (keys instanceof Set) ? (Set<K>) keys : new HashSet<>(keys);
    }

    public final Object get(final K key) {
        if (backing.containsKey(key)) {
            return backing.get(key);
        } else {
            throw new IllegalArgumentException("Illegal key: <" + key + ">");
        }
    }

    public final Map<K, Object> asMap() {
        return backing;
    }

    @Override
    public final boolean equals(final Object other) {
        return (this == other) || ((other instanceof Mapped<?>) && backing.equals(((Mapped<?>) other).backing));
    }

    @Override
    public final int hashCode() {
        return backing.hashCode();
    }

    @Override
    public final String toString() {
        return backing.toString();
    }

    public interface Key {
        Class<?> getValueClass();

        boolean isNullable();

        Object getDefault();
    }

    public static class Builder<K extends Mapped.Key, R> {
        private final Set<K> keySet;
        private final Map<K, Object> backing;
        private final Function<Map<? extends K, ?>, R> function;

        protected Builder(
                final Collection<? extends K> keys, final Map<? extends K, ?> origin,
                final Function<Map<? extends K, ?>, R> function, final boolean ignoreOverhead)
                throws IllegalArgumentException {

            this.keySet = unmodifiableSet(new HashSet<>(keys));
            this.backing = copy(keySet, origin, ignoreOverhead);
            this.function = function;
        }

        public final R build() {
            return function.apply(backing);
        }

        public final Builder<K, R> set(final K key, final Object value) {
            backing.put(key, cast(key, value));
            return this;
        }
    }
}
