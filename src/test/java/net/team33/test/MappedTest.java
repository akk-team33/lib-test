package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class MappedTest {

    private static final EnumSet<Key> KEY_SET_00 = EnumSet.allOf(Key.class);
    private static final EnumSet<Key> KEY_SET_01 = EnumSet.of(Key.STRING, Key.LIST, Key.INTEGER);
    private static final EnumSet<Key> KEY_SET_02 = EnumSet.of(Key.STRING, Key.DOUBLE, Key.DATE);

    @Test
    public void testMapped_ContainsAnyInitialKey_01() {
        final Map<Key, Object> original = Mapped.builder(KEY_SET_01)
                .build()
                .asMap();
        Assert.assertEquals(
                KEY_SET_00,
                new Mapped<>(KEY_SET_00, original)
                        .asMap()
                        .keySet()
        );
    }

    @Test
    public void testMapped_ContainsAnyInitialKey_02() {
        final Map<Key, Object> original = Mapped.builder(KEY_SET_02)
                .set(Key.STRING, "a string")
                .set(Key.DOUBLE, 3.141592654)
                .build()
                .asMap();
        Assert.assertEquals(
                KEY_SET_00,
                new Mapped<>(KEY_SET_00, original)
                        .asMap()
                        .keySet()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapped_ContainsAnyInitialKey_03() {
        final Map<Key, Object> original = Mapped.builder(KEY_SET_02)
                .set(Key.STRING, "a string")
                .set(Key.DOUBLE, 3.141592654)
                .build()
                .asMap();
        Assert.assertEquals(
                KEY_SET_01,
                new Mapped<>(KEY_SET_01, original) // -> IllegalArgument
                        .asMap()
                        .keySet()
        );
    }

    @Test
    public void testMapped_ContainsAnyInitialKey_04() {
        final Map<Key, Object> original = Mapped.builder(KEY_SET_02)
                .set(Key.STRING, "a string")
                .set(Key.DOUBLE, 3.141592654)
                .build()
                .asMap();
        Assert.assertEquals(
                KEY_SET_01,
                new Mapped<>(KEY_SET_01, original, true)
                        .asMap()
                        .keySet()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAsMap() throws Exception {
        final Map<Key, Object> origin = Mapped.builder(KEY_SET_02)
                .set(Key.STRING, "a string")
                .set(Key.DOUBLE, 3.141592654)
                .build().asMap();
        Mapped.builder(KEY_SET_01, origin);
    }

    private enum Key implements Mapped.Key {
        STRING {
            @Override
            public final Class<String> getValueClass() {
                return String.class;
            }
        },
        INTEGER {
            @Override
            public final Class<Integer> getValueClass() {
                return Integer.class;
            }
        },
        DOUBLE {
            @Override
            public final Class<Double> getValueClass() {
                return Double.class;
            }
        },
        DATE {
            @Override
            public final Class<Date> getValueClass() {
                return Date.class;
            }
        },
        LIST {
            @Override
            public final Class<List> getValueClass() {
                return List.class;
            }
        };

        @Override
        public final boolean isNullable() {
            return true;
        }

        @Override
        public final Object getDefault() {
            return null;
        }
    }
}
