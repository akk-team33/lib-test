package net.team33.test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

/**
 * Provides a basic implementation of an immutable data object with its properties represented by a specific
 * {@link Set} of keys and backed by a {@link Map} containing an entry for each of those keys.
 *
 * @param <K> The specific type of the keys representing the properties.
 */
public abstract class Mapped<K extends Mapped.Key> {

    private static final String ILLEGAL_KEY = "Illegal key <%s>";
    private static final String ILLEGAL_KEYS = "<origin> contains illegal keys: <%s>";
    private static final String VALUE_IS_NULL = "<value> must not be <null>";

    /**
     * Intended to initialize or update a map used as backing for a Mapped, a derivative or a relating Builder
     * @throws NullPointerException
     * @throws ClassCastException
     * @throws IllegalArgumentException
     */
    public static <K extends Key, M extends Map<K, Object>> M copy(
            final Map<? extends K, ?> origin, final Collection<? extends K> keySet,
            final boolean reset, final boolean ignoreOverhead, final M result) {

        if (ignoreOverhead || keySet.containsAll(origin.keySet())) {
            for (final K key : keySet) {
                final boolean containsKey = origin.containsKey(key);
                if (reset || containsKey) {
                    final Object value = containsKey ? origin.get(key) : key.getDefault();
                    result.put(key, valid(key, value));
                }
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
            // may cause a ClassCastException ...
            return key.getValueClass().cast(value);
        } else {
            // noinspection ProhibitedExceptionThrown
            throw new NullPointerException(VALUE_IS_NULL);
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

        /**
         * Supplies the {@linkplain Class class representation} of values that may be associated with this key.
         */
        Class<?> getValueClass();

        /**
         * Indicates weather or not {@code null} may be associated with this key.
         */
        boolean isNullable();

        /**
         * Supplies a default value to be initially associated with this key.
         */
        Object getDefault();
    }

    /**
     * Abstracts a mutable counterpart to a {@link Mapped}
     * intended to be derived as a Builder for a derivation of {@link Mapped}.
     *
     * @param <K> The specific type of the keys representing the properties.
     * @param <B> The final derivation of this class
     */
    @SuppressWarnings("PublicInnerClass")
    public abstract static class Composer<K extends Enum<K> & Key, B extends Composer<K, B>> {

        /**
         * Sets the {@code value} for a specific {@code key}, if it's part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if {@code value} is {@code null} and the specified {@code key}
         *                                  is not {@linkplain Key#isNullable() nullable}.
         * @throws ClassCastException       if {@code value} is not assignable to the {@linkplain Key#getValueClass()
         *                                  class} associated with the specified {@code key}.
         * @throws IllegalArgumentException if the specified {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set}.
         */
        public final B set(final K key, final Object value) {
            return set(key, value, false);
        }

        /**
         * Sets the {@code value} for a specific {@code key}, if it's part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores the value or throws an IllegalArgumentException.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if {@code value} is {@code null} and the specified {@code key} indicates
         *                                  to be not {@linkplain Key#isNullable() nullable}.
         * @throws ClassCastException       if {@code value} is not assignable to the {@linkplain Key#getValueClass()
         *                                  value class} associated with the specified {@code key}.
         * @throws IllegalArgumentException if the specified {@code key} is not part of the {@linkplain #keySet()}
         *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
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
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will remain as is.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set}.
         */
        public final B set(final Map<? extends K, ?> origin) {
            return set(origin, false);
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will remain as is.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
         */
        public final B set(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
            copy(origin, keySet(), false, ignoreOverhead, asMap());
            return self();
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will be reset to their
         * {@linkplain Key#getDefault() defaults}.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set}.
         */
        public final B reset(final Map<? extends K, ?> origin) {
            return set(origin, false);
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will be reset to their
         * {@linkplain Key#getDefault() defaults}.
         *
         * @return {@code this} in its final representation.
         * @throws NullPointerException     if a {@code value} is {@code null} and the corresponding {@code key}
         *                                  is not {@linkplain Key#isNullable() nullable}.
         * @throws ClassCastException       if a {@code value} is not assignable to the {@linkplain Key#getValueClass()
         *                                  class} associated with the corresponding {@code key}.
         * @throws IllegalArgumentException if an original {@code key} is not part of the {@linkplain #keySet()
         *                                  intended key set} and {@code ignoreOverhead} is {@code false}.
         */
        public final B reset(final Map<? extends K, ?> origin, final boolean ignoreOverhead) {
            copy(origin, keySet(), true, ignoreOverhead, asMap());
            return self();
        }

        /**
         * Supplies a convenient string representation.
         */
        @Override
        public final String toString() {
            return getClass().getName() + asMap().toString();
        }

        /**
         * Supplies the intended key set.
         * <p/>
         * Must be a separate immutable set - not simply the {@link Map#keySet()} for the {@linkplain #asMap()
         * underlying map} - otherwise expect strange side effects!
         */
        protected abstract Set<K> keySet();

        /**
         * Supplies the mutable (!) Map this instance is backed by.
         */
        protected abstract Map<K, Object> asMap();

        /**
         * Supplies {@code this} in its final representation.
         */
        protected abstract B self();
    }
}
