package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;

public class EnumMappedTest {

    private static final Map<? extends Key, ?> EMPTY_MAP = emptyMap();

    private static Builder builder(final Key... keys) {
        return new Builder(asList(keys));
    }

    @Test
    public void test_EnumMapped_Map() {
        final Map<Key, Object> template = builder(Key.STRING, Key.INTEGER)
                .set(Key.STRING, "a string")
                .set(Key.INTEGER, 278)
                .asMap();
        final EnumMapped<Key> subject = new EnumMapped<>(template);
        Assert.assertEquals(
                template,
                subject.asMap()
        );
    }

    @Test(expected = NoSuchElementException.class)
    public void test_EnumMapped_Map__empty() {
        Assert.assertNull(
                "Should not happen :-o",
                new EnumMapped<>(EMPTY_MAP)
        );
    }

    @Test(expected = NullPointerException.class)
    public void test_EnumMapped_Map__null() {
        Assert.assertNull(
                "Should not happen :-o",
                new EnumMapped<Key>(null)
        );
    }

    @Test
    public void test_EnumMapped_Class_Map__Stuff_Empty() {
        final EnumMapped<Key> subject = new EnumMapped<>(Key.class, EMPTY_MAP);
        Assert.assertFalse(
                "<subject> should not be empty",
                subject.asMap().isEmpty()
        );
        for (final Key key : Key.values()) {
            Assert.assertTrue(
                    "<subject> should contain key <" + key + ">",
                    subject.asMap().containsKey(key)
            );
            Assert.assertEquals(
                    key.getInitial(),
                    subject.asMap().get(key)
            );
        }
    }

    @Test
    public void test_Mapper_Class() {
        final Builder subject = new Builder(Key.class);
        for (final Key key : Key.values()) {
            Assert.assertTrue(
                    "<subject> should contain key <" + key + ">",
                    subject.asMap().containsKey(key)
            );
            Assert.assertEquals(
                    key.getInitial(),
                    subject.asMap().get(key)
            );
        }
    }

    @Test
    public void test_EnumMapped_Class_Map() {
        final Map<Key, Object> template = builder(Key.STRING, Key.INTEGER)
                .set(Key.STRING, "a string")
                .set(Key.INTEGER, 278)
                .asMap();
        final EnumMapped<Key> subject = new EnumMapped<>(Key.class, template);
        Assert.assertTrue(
                "<subject> should contain each entries of <template>",
                subject.asMap().entrySet().containsAll(template.entrySet())
        );
        for (final Key key : Key.values()) {
            if (!template.containsKey(key)) {
                Assert.assertTrue(
                        "<subject> should contain key <" + key + ">",
                        subject.asMap().containsKey(key)
                );
                Assert.assertEquals(
                        key.getInitial(),
                        subject.asMap().get(key)
                );
            }
        }
    }

    private enum Key implements Mapped.Key {
        //IMPOSSIBLE(Object.class, false, null),
        STRING(String.class, false, ""),
        INTEGER(Integer.class, false, 0),
        DOUBLE(Double.class, true, 0.0),
        DATE(Date.class, true, new Date(0));

        private final Class<?> valueClass;
        private final boolean nullable;
        private final Object fallback;

        Key(final Class<?> valueClass, final boolean nullable, final Object fallback) {
            this.valueClass = valueClass;
            this.nullable = nullable;
            this.fallback = fallback;
        }

        @Override
        public Class<?> getValueClass() {
            return valueClass;
        }

        @Override
        public boolean isNullable() {
            return nullable;
        }

        @Override
        public Object getInitial() {
            return fallback;
        }
    }

    private static class Builder extends EnumMapped.Setter<Key, Builder> {
        private Builder(final Collection<Key> keys) {
            super(keys);
        }

        private Builder(final Class<Key> keyClass) {
            super(keyClass);
        }

        @Override
        protected final Builder finallyThis() {
            return this;
        }
    }
}
