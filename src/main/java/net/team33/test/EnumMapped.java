package net.team33.test;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.allOf;
import static java.util.EnumSet.copyOf;

public class EnumMapped<K extends Enum<K> & Mapped.Key> extends Mapped<K> {

    private final Map<K, Object> backing;

    protected EnumMapped(final Class<K> keyClass,
                         final Collection<? extends K> keys,
                         final Map<? extends K, ?> template) {

        backing = unmodifiableMap(copy(template, keys, true, false, new EnumMap<>(keyClass)));
    }

    @Override
    public final Map<K, Object> asMap() {
        // It's already immutable ...
        // noinspection ReturnOfCollectionOrArrayField
        return backing;
    }

    @SuppressWarnings("PublicInnerClass")
    public abstract static class Mapper<K extends Enum<K> & Mapped.Key, B extends Mapper<K, B>>
            extends Mapped.Composer<K, B> {

        private final Set<K> keys;
        private final Map<K, Object> backing;

        protected Mapper(final Class<K> keyClass) {
            this(keyClass, allOf(keyClass), Collections.<K, Object>emptyMap());
        }

        protected Mapper(final Class<K> keyClass, final EnumSet<K> keys, final Map<? extends K, ?> template) {
            this.keys = unmodifiableSet(copyOf(keys));
            backing = copy(template, this.keys, true, true, new EnumMap<>(keyClass));
        }

        @Override
        protected final Set<K> keySet() {
            // It's already immutable ...
            // noinspection ReturnOfCollectionOrArrayField
            return keys;
        }

        @Override
        protected final Map<K, Object> asMap() {
            // It's intended to be modifiable ...
            // noinspection ReturnOfCollectionOrArrayField
            return backing;
        }
    }
}
