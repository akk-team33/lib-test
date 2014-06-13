package net.team33.test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * An abstract implementation of an immutable data object with its properties represented by a specific
 * {@link Set} of keys and backed by a {@link Map} containing an entry for each of those keys.
 *
 * @param <K> The specific type of the keys representing the properties.
 */
public abstract class Mapped<K extends Mapped.Key> {

    private static final String ILLEGAL_KEY = "Illegal key <%s>";
    private static final String ILLEGAL_KEYS = "<origin> contains illegal keys: <%s>";

    /**
     * @throws NullPointerException
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    public static <K extends Key, M extends Map<K, Object>> M copy(
            final Map<? extends K, ?> origin, final Collection<? extends K> keySet,
            final boolean ignoreOverhead, final M result) {
        if (ignoreOverhead || keySet.containsAll(origin.keySet())) {
            for (final K key : keySet) {
                final Object value = origin.containsKey(key) ? origin.get(key) : key.getDefault();
                result.put(key, valid(key, value));
            }
            return result;
        } else {
            throw new IllegalArgumentException(format(ILLEGAL_KEYS, origin.keySet()));
        }
    }

    /**
     * @throws NullPointerException
     * @throws ClassCastException
     */
    private static Object valid(final Key key, final Object value) {
        if (null != value || key.isNullable()) {
            return key.getValueClass().cast(value);
        } else {
            //noinspection ProhibitedExceptionThrown
            throw new NullPointerException("<value> must not be <null>");
        }
    }

    /**
     * Supplies an immutable Map representing the properties of this instance.
     */
    public abstract Map<K, Object> asMap();

    /**
     * Retrieves the specified property value.
     *
     * @param key The property specification.
     * @throws NullPointerException     (optional)
     *                                  if {@code key} is {@code null} and {@code null} is not supported by the
     *                                  underlying map.
     * @throws IllegalArgumentException if the underlying map does not contain the specified {@code key}.
     */
    public final Object get(final K key) {
        if (asMap().containsKey(key)) {
            return asMap().get(key);
        } else {
            throw new IllegalArgumentException(format(ILLEGAL_KEY, key));
        }
    }

    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Mapped<?>) && asMap().equals(((Mapped<?>) obj).asMap()));
    }

    @Override
    public final int hashCode() {
        return asMap().hashCode();
    }

    @Override
    public final String toString() {
        return asMap().toString();
    }

    @SuppressWarnings("InterfaceNamingConvention")
    public interface Key {

        Class<?> getValueClass();

        boolean isNullable();

        Object getDefault();
    }

    @SuppressWarnings("PublicInnerClass")
    public abstract static class Composer<K extends Enum<K> & Key, B extends Composer<K, B>> {

        /**
         * @throws NullPointerException
         * @throws ClassCastException
         * @throws IllegalArgumentException
         */
        public final B set(final K key, final Object value) {
            return set(key, value, false);
        }

        /**
         * @throws NullPointerException
         * @throws ClassCastException
         * @throws IllegalArgumentException
         */
        public final B set(final K key, final Object value, final boolean ignoreOverhead) {
            if (keySet().contains(key)) {
                asMap().put(key, valid(key, value));
            } else if (!ignoreOverhead) {
                throw new IllegalArgumentException(format(ILLEGAL_KEY, key));
            }
            return self();
        }

        /**
         * @throws NullPointerException
         * @throws ClassCastException
         * @throws IllegalArgumentException
         */
        public final B set(final Map<? extends K, ?> origin) {
            return set(origin, false);
        }

        /**
         * @throws NullPointerException
         * @throws ClassCastException
         * @throws IllegalArgumentException
         */
        public final B set(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
            copy(origin, keySet(), ignoreOverhead, asMap());
            return self();
        }

        @Override
        public final String toString() {
            return getClass().getName() + asMap().toString();
        }

        /**
         * Supplies the supported keys in a separate {@link Set} (not {@link Map#keySet()} for {@link #asMap()}).
         */
        protected abstract Set<K> keySet();

        /**
         * Supplies the mutable Map this instance is backed by.
         */
        protected abstract Map<K, Object> asMap();

        protected abstract B self();
    }
}
