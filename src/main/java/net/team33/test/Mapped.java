package net.team33.test;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

public class Mapped<K extends Enum<K> & Mapped.Key> {

    private static final String VALUE_MUST_NOT_BE_NULL
            = "<value> must not be <null>";
    private static final String ORIGIN_DOES_NOT_MATCH_ITS_REQUIREMENTS
            = "At least one entry of <origin> doesn't match it's requirements: ";

    public static <K extends Enum<K> & Key> Builder<K, Mapped<K>> builder(final Class<K> keyClass) {
        return builder(keyClass, emptyMap());
    }

    public static <K extends Enum<K> & Key> Builder<K, Mapped<K>> builder(
            final Class<K> keyClass, final Map<? extends K, Object> origin) {

        return new Builder<>(keyClass, origin, template -> new Mapped<>(keyClass, template));
    }

    private static <K extends Enum<K> & Key> EnumMap<K, Object> copy(
            final Class<K> keyClass, final Map<? extends K, Object> origin) {

        try {
            final EnumMap<K, Object> result = new EnumMap<>(keyClass);
            for (final K key: EnumSet.allOf(keyClass)) {
                result.put(key, cast(key, origin.get(key)));
            }
            return result;

        } catch (final ClassCastException | NullPointerException caught) {
            throw new IllegalArgumentException(
                    ORIGIN_DOES_NOT_MATCH_ITS_REQUIREMENTS + origin, caught);
        }
    }

    private static <K extends Enum<K> & Key> Object cast(final K key, final Object value) {
        if (null != value || key.isNullable()) {
            return key.getValueClass().cast(value);
        } else {
            throw new NullPointerException(VALUE_MUST_NOT_BE_NULL);
        }
    }

    private final Map<K, Object> backing;

    public Mapped(final Class<K> keyClass, final Map<? extends K, Object> origin) {
        this.backing = unmodifiableMap(copy(keyClass, origin));
    }

    public final Object get(final K key) {
        return backing.get(key);
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
    }

    public static class Builder<K extends Enum<K> & Mapped.Key, R> {
        private final EnumMap<K, Object> backing;
        private final Class<K> keyClass;
        private final Function<Map<? extends K, Object>, R> function;

        protected Builder(
                final Class<K> keyClass, final Map<? extends K, Object> origin,
                final Function<Map<? extends K, Object>, R> function) {

            this.backing = copy(keyClass, origin);
            this.keyClass = keyClass;
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
