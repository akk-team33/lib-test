package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

public class MappedTest {

    private static final String VALUE_01 = "a string";

    static Builder builder(KEY... keys) {
        return new Builder(asList(keys));
    }

    @Test
    public final void testGet_previously_set() {
        final Subject subject = builder(KEY.STRING)
                .set(KEY.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                VALUE_01,
                subject.get(KEY.STRING)
        );
    }

    @Test
    public final void testSet_Map() {
        final Map<KEY, Object> origin = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, "a string")
                .set(KEY.INTEGER, 278)
                .asMap();
        Assert.assertEquals(
                origin,
                builder(KEY.STRING, KEY.INTEGER)
                        .set(origin)
                        .asMap()
        );
    }

    @Test
    public final void testReset_Map() {
        final Map<KEY, Object> expected = builder(KEY.STRING, KEY.INTEGER, KEY.DATE)
                .set(KEY.STRING, "a string")
                .set(KEY.INTEGER, 278)
                .asMap();
        final Map<KEY, Object> origin = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, "a string")
                .set(KEY.INTEGER, 278)
                .asMap();
        Assert.assertEquals(
                expected,
                builder(KEY.STRING, KEY.INTEGER, KEY.DATE)
                        .set(KEY.DATE, new Date())
                        .reset(origin)
                        .asMap()
        );
    }

    @Test
    public final void testGet_default() {
        final Subject subject = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                KEY.INTEGER.getInitial(),
                subject.get(KEY.INTEGER)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGet_foreign_key() {
        final Subject subject = builder(KEY.STRING)
                .set(KEY.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                KEY.INTEGER.getInitial(),
                subject.get(KEY.INTEGER)
        );
    }

    @SuppressWarnings({"ClassNameSameAsAncestorName", "EnumeratedClassNamingConvention"})
    private enum KEY implements Mapped.Key {

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
        },

        DATE {
            @Override
            public Class<Date> getValueClass() {
                return Date.class;
            }
        };

        @Override
        public boolean isNullable() {
            return true;
        }

        @Override
        public Object getInitial() {
            return null;
        }
    }

    private static class Subject extends Mapped<KEY> {

        private final Map<KEY, Object> backing;

        private Subject(final Map<? extends KEY, ?> template, final boolean ignoreOverhead) {
            backing = unmodifiableMap(copy(template, template.keySet(), true, ignoreOverhead, new HashMap<>(0)));
        }

        @Override
        public final Map<KEY, Object> asMap() {
            return backing;
        }
    }

    @SuppressWarnings("ReturnOfThis")
    private static class Builder extends Mapped.Mutable<KEY, Builder> {

        private final Set<KEY> keys;
        private final Map<KEY, Object> backing;

        private Builder(final Collection<? extends KEY> keys) {
            this.keys = unmodifiableSet(new HashSet<>(keys));
            backing = Mapped.copy(Collections.EMPTY_MAP, this.keys, true, false, new HashMap<>(0));
        }

        @Override
        protected final Set<KEY> keySet() {
            return keys;
        }

        @Override
        public final Map<KEY, Object> asMap() {
            return backing;
        }

        @Override
        protected Builder finallyThis() {
            return this;
        }

        public Subject build() {
            return new Subject(asMap(), true);
        }
    }
}
