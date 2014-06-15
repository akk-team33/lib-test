package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;

public class EnumMappedTest {

    private static Builder builder(final KEY... keys) {
        return new Builder(asList(keys));
    }

    @Test
    public void test_EnumMapped_Map() {
        final Builder template = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, "a string")
                .set(KEY.INTEGER, 278);
        final EnumMapped<KEY> subject = new EnumMapped<>(template);
        Assert.assertEquals(
                template.asMap(),
                subject.asMap()
        );
    }

    @Test(expected = NoSuchElementException.class)
    public void test_EnumMapped_Map__empty() {
        Assert.assertNull(
                "Should not happen :-o",
                new EnumMapped<>(builder())
        );
    }

    @Test(expected = NullPointerException.class)
    public void test_EnumMapped_Map__null() {
        Assert.assertNull(
                "Should not happen :-o",
                new EnumMapped<KEY>(null)
        );
    }

    @Test
    public void test_EnumMapped_Class_Map__Stuff_Empty() {
        final EnumMapped<KEY> subject = new EnumMapped<>(new Builder(KEY.class));
        Assert.assertFalse(
                "<subject> should not be empty",
                subject.asMap().isEmpty()
        );
        for (final KEY key : KEY.values()) {
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
        final Builder subject = new Builder(KEY.class);
        for (final KEY key : KEY.values()) {
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
        final Map<KEY, Object> template = builder(KEY.STRING, KEY.INTEGER)
                .set(KEY.STRING, "a string")
                .set(KEY.INTEGER, 278)
                .asMap();
        final EnumMapped<KEY> subject = new EnumMapped<>(new Builder(KEY.class).set(template));
        Assert.assertTrue(
                "<subject> should contain each entries of <template>",
                subject.asMap().entrySet().containsAll(template.entrySet())
        );
        for (final KEY key : KEY.values()) {
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

    private enum KEY implements Mapped.Key {
        //IMPOSSIBLE(Object.class, false, null),
        STRING(String.class, false, ""),
        INTEGER(Integer.class, false, 0),
        DOUBLE(Double.class, true, 0.0),
        DATE(Date.class, true, new Date(0));

        private final Class<?> valueClass;
        private final boolean nullable;
        private final Object fallback;

        KEY(final Class<?> valueClass, final boolean nullable, final Object fallback) {
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

    private static class Builder extends EnumMapped.Mapper<KEY, Builder> {
        private Builder(final Collection<KEY> keys) {
            super(keys);
        }

        private Builder(final Class<KEY> keyClass) {
            super(keyClass);
        }

        @Override
        protected final Builder finallyThis() {
            return this;
        }
    }
}
