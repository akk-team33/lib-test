package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

public class MappedTest {

    private static final String VALUE_01 = "a string";

    @Test
    public final void testGet_previously_set() {
        final Subject subject = Subject.builder()
                .set(Key.STRING, VALUE_01)
                .build();
        Assert.assertEquals(
                VALUE_01,
                subject.get(Key.STRING)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testGet_foreign_key() {
        final Subject subject = Subject.builder()
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

    private static class Subject extends EnumMapped<Key> {

        Subject(final Collection<? extends MappedTest.Key> keys, final Map<? extends MappedTest.Key, ?> template) {
            super(MappedTest.Key.class, keys, template);
        }

        static Builder builder() {
            return new Builder();
        }
    }

    @SuppressWarnings("ReturnOfThis")
    private static class Builder extends EnumMapped.Mapper<Key, Builder> {
        protected Builder() {
            super(Key.class, EnumSet.of(Key.STRING), Collections.<Key, Object>emptyMap());
        }

        public Subject build() {
            return new Subject(keySet(), asMap());
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}