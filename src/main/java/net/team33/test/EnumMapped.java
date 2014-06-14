package net.team33.test;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.allOf;
import static java.util.EnumSet.copyOf;

public class EnumMapped<K extends Enum<K> & Mapped.Key> extends Mapped<K> {

    private final Map<K, Object> backing;

    /**
     * Initiates a new instance backed by a copy of a given template map.
     *
     * @param template The template map, not {@code null} and not {@linkplain Map#isEmpty() empty}!
     * @throws NullPointerException   if {@code template} is {@code null}.
     * @throws NoSuchElementException if {@code template} is {@linkplain Map#isEmpty() empty}.
     */
    protected EnumMapped(final Map<? extends K, ?> template) {
        // There must be at least one key - else -> NoSuchElementException ...
        // There cannot be any overhead, so may be frankly ignored ...
        this(template.keySet().iterator().next().getDeclaringClass(), template.keySet(), template, true);
    }

    /**
     * Initiates a new instance backed by a copy of a given {@code template} map, that finally contains any possible
     * key of a given class but not {@code null}.
     * <p/>
     * Values for keys not specified by the {@code template} will be set by {@linkplain Key#getInitial() default}.
     *
     * @param keyClass The {@linkplain Class class representation} of the intended keys, not {@code null}.
     * @param template The template map, may be empty but not {@code null}.
     * @throws NullPointerException if {@code keyClass} or {@code template} is {@code null}.
     */
    protected EnumMapped(final Class<K> keyClass, final Map<? extends K, ?> template) {
        // There cannot be any overhead, so may be frankly ignored ...
        this(keyClass, allOf(keyClass), template, true);
    }

    /**
     * Initiates a new instance backed by a copy of a given {@code template} map as far as it's keys are part of the
     * given {@code keySet}. Otherwise either ignores overhead values or throws an IllegalArgumentException.
     * <p/>
     * Finally the resulting instance contains each key from the given {@code keySet}. Values for keys not specified
     * by the {@code template} will be set by {@linkplain Key#getInitial() default}.
     */
    protected EnumMapped(final Class<K> keyClass, final Collection<? extends K> keys,
                         final Map<? extends K, ?> template, final boolean ignoreOverhead) {
        backing = unmodifiableMap(Mapped.copy(template, keys, true, ignoreOverhead, new EnumMap<>(keyClass)));
    }

    @Override
    public final Map<K, Object> asMap() {
        // Already is immutable ...
        // noinspection ReturnOfCollectionOrArrayField
        return backing;
    }

    /**
     * Provides basic implementations of a mutable counterpart to a {@link EnumMapped}
     * intended to be derived as a Builder for a derivation of {@link EnumMapped}.
     *
     * @param <K> The specific type of the keys representing the properties.
     * @param <B> The final (relevant) derivation of this class
     */
    @SuppressWarnings("PublicInnerClass")
    public abstract static class Setter<K extends Enum<K> & Mapped.Key, B extends Setter<K, B>>
            extends Mapped.Setter<K, B> {

        private final Set<K> keys;
        private final Map<K, Object> backing;

        /**
         * Initiates a new instance by a given {@code keyClass} that will contain any possible key but {@code null},
         * associated with their {@linkplain Key#getInitial() default values}.
         *
         * @param keyClass The {@linkplain Class class representation} of the intended keys, not {@code null}.
         * @throws NullPointerException if {@code keyClass} is {@code null}.
         */
        protected Setter(final Class<K> keyClass) {
            this(keyClass, allOf(keyClass));
        }

        /**
         * Initiates a new instance by a given {@code keySet} that will contain any possible key but {@code null},
         * associated with their {@linkplain Key#getInitial() default values}.
         *
         * @param keySet The {@linkplain Class class representation} of the intended keys, not {@code null}.
         * @throws NullPointerException     if {@code keySet} is or contains {@code null}.
         * @throws IllegalArgumentException if {@code keySet} is empty and not an instance of {@link EnumSet}.
         */
        protected Setter(final Collection<K> keySet) {
            this(keySet.iterator().next().getDeclaringClass(), keySet);
        }

        /**
         * @throws NullPointerException     if {@code keyClass} or {@code keySet} is or contains {@code null}.
         * @throws IllegalArgumentException if {@code keySet} is empty and not an instance of {@link EnumSet}.
         */
        @SuppressWarnings("unchecked")
        private Setter(final Class<K> keyClass, final Collection<K> keySet) {
            this.keys = unmodifiableSet(copyOf(keySet));
            backing = copy(EMPTY_MAP, this.keys, true, true, new EnumMap<>(keyClass));
        }

        @Override
        protected final Set<K> keySet() {
            // Already is immutable ...
            // noinspection ReturnOfCollectionOrArrayField
            return keys;
        }

        @Override
        protected final Map<K, Object> asMap() {
            // Intended to be modifiable ...
            // noinspection ReturnOfCollectionOrArrayField
            return backing;
        }
    }
}
