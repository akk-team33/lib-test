package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

public class MappedTest {

    private static final String VALUE_01 = "a string";

    static Builder builder(Key... keys) {
        return new Builder(asList(keys));
    }

    @Test
    public final void testGet_previously_set() {
        final Subject subject = builder(Key.STRING)
                .set(Key.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                VALUE_01,
                subject.get(Key.STRING)
        );
    }

    @Test
    public final void testGet_default() {
        final Subject subject = builder(Key.STRING, Key.INTEGER)
                .set(Key.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                Key.INTEGER.getDefault(),
                subject.get(Key.INTEGER)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGet_foreign_key() {
        final Subject subject = builder(Key.STRING)
                .set(Key.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                Key.INTEGER.getDefault(),
                subject.get(Key.INTEGER)
        );
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "EnumeratedClassNamingConvention"})
    private enum Key implements Mapped.Key {

        STRING {
            @Override
            public Class<String> getValueClass() {
                return String.class;
            }
        },

        INTEGER {
            @Override
            public Class<Integer> getValueClass() {
                return Integer.class;
            }
        };

        @Override
        public boolean isNullable() {
            return true;
        }

        @Override
        public Object getDefault() {
            return null;
        }
    }

    private static class Subject extends Mapped<MappedTest.Key> {

        private final Map<MappedTest.Key, Object> backing;

        private Subject(final Map<? extends MappedTest.Key, ?> template, final boolean ignoreOverhead) {
            backing = unmodifiableMap(copy(template, template.keySet(), true, ignoreOverhead, new HashMap<>(0)));
        }

        @Override
        public final Map<MappedTest.Key, Object> asMap() {
            return backing;
        }
    }

    @SuppressWarnings("ReturnOfThis")
    private static class Builder extends Mapped.Composer<MappedTest.Key, Builder> {
        private static final Map<? extends Key, ?> EMPTY_MAP = Collections.emptyMap();

        private final Set<Key> keys;
        private final Map<Key, Object> backing;

        private Builder(final Collection<? extends Key> keys) {
            this.keys = unmodifiableSet(new HashSet<>(keys));
            backing = Mapped.copy(EMPTY_MAP, this.keys, true, false, new HashMap<>(0));
        }

        @Override
        protected final Set<Key> keySet() {
            return keys;
        }

        @Override
        protected final Map<Key, Object> asMap() {
            return backing;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Subject build() {
            return new Subject(asMap(), true);
        }
    }
}
