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

/**
 * A generic implementation of an immutable data object with its properties represented by a specific
 * {@link Set} of keys and backed by a {@link Map} {@linkplain Map#keySet() containing} any of those keys.
 *
 * @param <K> The specific type of the keys representing the properties.
 */
@SuppressWarnings("UnusedDeclaration")
public class Mapped<K extends Mapped.Key> {

    private static final String VALUE_MUST_NOT_BE_NULL
            = "<value> must not be <null>";
    private static final String ORIGIN_DOES_NOT_MATCH_ITS_REQUIREMENTS
            = "At least one entry of <origin> doesn't match it's requirements: ";

    private final Map<K, Object> backing;

    /**
     * Initiates an instance directly through a given {@link Set} of keys representing its properties
     * and an original {@link Map} containing the presumed values for those properties.
     * <p/>
     * The original map may NOT contain keys not present in the given set of keys.
     *
     * @param keys   Not {@code null}. May be empty, thus a resulting instance will not have any property.
     * @param origin Not {@code null}. May be empty or may {@linkplain Map#keySet() contain}
     *               a subset of the given keys.
     * @throws NullPointerException     When an argument is {@code null}.
     * @throws IllegalArgumentException When an original value does not meet the requirements of its associated key
     *                                  or original keys are overhead.
     */
    public Mapped(final Collection<? extends K> keys, final Map<? extends K, ?> origin)
            throws NullPointerException, IllegalArgumentException {

        this(keys, origin, false);
    }

    /**
     * Initiates an instance directly through a given {@link Set} of keys representing its properties
     * and an original {@link Map} containing the presumed values for those properties.
     *
     * @param keys           Not {@code null}. May be empty, thus a resulting instance will not have any property.
     * @param origin         Not {@code null}. May be empty or may {@linkplain Map#keySet() contain}
     *                       a subset of the given keys.
     * @param ignoreOverhead <ul>
     *                       <li>{@code true}: The original map may contain keys not present in the given set of keys.
     *                       Those keys and their associated values will be ignored by the resulting instance.</li>
     *                       <li>{@code false}: The original map may NOT contain keys not present in the given set of
     *                       keys.</li>
     *                       </ul>
     * @throws NullPointerException     When an argument is {@code null}.
     * @throws IllegalArgumentException When an original value does not meet the requirements of its associated key
     *                                  or original keys are overhead but not ignorable.
     */
    public Mapped(final Collection<? extends K> keys, final Map<? extends K, ?> origin, final boolean ignoreOverhead)
            throws NullPointerException, IllegalArgumentException {

        this.backing = unmodifiableMap(
                copy(toSet(keys), origin, ignoreOverhead, keySet -> new HashMap<>(keySet.size())));
    }

    public static <K extends Key> Builder<K, Mapped<K>> builder(final Collection<? extends K> keys)
            throws NullPointerException, IllegalArgumentException {

        return builder(keys, emptyMap());
    }

    public static <K extends Key> Builder<K, Mapped<K>> builder(
            final Collection<? extends K> keys, final Map<? extends K, ?> origin)
            throws NullPointerException, IllegalArgumentException {

        return builder(keys, origin, false);
    }

    /**
     * Retrieves a new {@link Builder} through a given {@link Set} of keys representing its properties
     * and an original {@link Map} containing the presumed values for those properties.
     *
     * @param keys           Not {@code null}. May be empty, thus a resulting instance will not have any property.
     * @param origin         Not {@code null}. May be empty or may {@linkplain Map#keySet() contain}
     *                       a subset of the given keys.
     * @param ignoreOverhead <ul>
     *                       <li>{@code true}: The original map may contain keys not present in the given set of keys.
     *                       Those keys and their associated values will be ignored by the resulting instance.</li>
     *                       <li>{@code false}: The original map may NOT contain keys not present in the given set of
     *                       keys.</li>
     *                       </ul>
     * @throws NullPointerException     When an argument is {@code null}.
     * @throws IllegalArgumentException When an original value does not meet the requirements of its associated key
     *                                  or original keys are overhead but not ignorable.
     */
    public static <K extends Key> Builder<K, Mapped<K>> builder(
            final Collection<? extends K> keys, final Map<? extends K, ?> origin, final boolean ignoreOverhead)
            throws NullPointerException, IllegalArgumentException {

        return new Builder<>(keys, origin, template -> new Mapped<>(keys, template, true), ignoreOverhead);
    }

    private static <K extends Key> Map<K, Object> copy(
            final Set<K> keySet, final Map<? extends K, ?> origin, final boolean ignoreOverhead,
            final Function<Set<K>, Map<K, Object>> newMap)
            throws IllegalArgumentException {

        if (ignoreOverhead || keySet.containsAll(origin.keySet())) {
            try {
                final Map<K, Object> result = newMap.apply(keySet);
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
            this.backing = copy(keySet, origin, ignoreOverhead, keySet -> new HashMap<>(keySet.size()));
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
