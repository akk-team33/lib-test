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
     * Intended to initialize or update a map used as backing for a Mapped, a derivative or a relating Builder.
     *
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
                    final Object value = containsKey ? origin.get(key) : key.getInitial();
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
     * Supplies a Map representing the properties of this instance.
     * Commonly this Map may and should be immutable.
     */
    public abstract Map<K, Object> asMap();

    /**
     * Retrieves the specified property value. Intended to easily implement a property specific, well typed getter.
     *
     * @param key The property specification.
     * @throws NullPointerException     (optional)
     *                                  if {@code key} is {@code null} and {@code null} is not supported by the
     *                                  underlying map.
     * @throws IllegalArgumentException if the underlying map does not contain the specified {@code key}.
     * @throws ClassCastException       if not applied in the correct class context.
     */
    public final <T> T get(final K key) {
        if (asMap().containsKey(key)) {
            // Causes a ClassCastException just like an explicit outer cast which otherwise were necessary ...
            // noinspection unchecked
            return (T) asMap().get(key);
        } else {
            throw new IllegalArgumentException(format(ILLEGAL_KEY, key));
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation assumes equality simply depending on the {@linkplain #asMap() underlying map}.
     */
    @Override
    public final boolean equals(final Object obj) {
        return (this == obj) || ((obj instanceof Mapped<?>) && asMap().equals(((Mapped<?>) obj).asMap()));
    }

    /**
     * {@inheritDoc}
     * <p/>
     * This implementation retrieves the hash code simply from the {@linkplain #asMap() underlying map}.
     */
    @Override
    public final int hashCode() {
        return asMap().hashCode();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * The default implementation supplies the {@linkplain Map#toString() string representation} of the
     * {@linkplain #asMap() underlying map}, prefixed by {@link #toStringPrefix()}.
     * <p/>
     * If desired a derivative may override (replace or modify) this implementation
     * (or simply override {@link #toStringPrefix()}).
     */
    @Override
    public String toString() {
        return toStringPrefix() + asMap().toString();
    }

    /**
     * Supplies a prefix used by {@link #toString()} (while not overridden itself).
     * <p/>
     * The default implementation supplies the simple name of this' {@linkplain #getClass() class representation}.
     * <p/>
     * If desired a derivative may override (replace or modify) this implementation.
     */
    protected String toStringPrefix() {
        return getClass().getSimpleName();
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
        Object getInitial();
    }

    /**
     * Provides basic implementations of a mutable counterpart to a {@link Mapped}
     * intended to be derived as a Builder for a derivation of {@link Mapped}.
     *
     * @param <K> The specific type of the keys representing the properties.
     * @param <B> The final (relevant) derivation of this class
     */
    @SuppressWarnings("PublicInnerClass")
    public abstract static class Mutable<K extends Key, B extends Mutable<K, B>> extends Mapped<K> {

        /**
         * {@inheritDoc}
         * <p/>
         * In contrast to the common specification, a Mutable implementation must supply a MUTABLE map!
         */
        @Override
        public abstract Map<K, Object> asMap();

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
            return finallyThis();
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
            return finallyThis();
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will be reset to their
         * {@linkplain Key#getInitial() defaults}.
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
            return reset(origin, false);
        }

        /**
         * Sets the values according to an origin map, as far as it's keys are part of the {@linkplain #keySet()
         * intended key set}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
         * <p/>
         * Values associated with keys not covered by the origin map will be reset to their
         * {@linkplain Key#getInitial() defaults}.
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
            return finallyThis();
        }

        /**
         * Supplies the intended key set.
         * <p/>
         * Must be a separate immutable set - not simply the {@link Map#keySet()} for the {@linkplain #asMap()
         * underlying map} - otherwise expect strange side effects!
         */
        protected abstract Set<K> keySet();

        /**
         * Supplies {@code this} in its final representation.
         */
        protected abstract B finallyThis();
    }
}
