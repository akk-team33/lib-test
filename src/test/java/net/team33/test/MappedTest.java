package net.team33.test;

import org.junit.Test;

import javax.print.DocFlavor;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MappedTest {

    @Test(expected = IllegalArgumentException.class)
    public void testAsMap() throws Exception {
        final Map<Key, Object> origin = Mapped.builder(EnumSet.of(Key.STRING, Key.DOUBLE))
                .set(Key.STRING, "a string")
                .set(Key.DOUBLE, 3.141592654)
                .build().asMap();
        Mapped.builder(EnumSet.of(Key.STRING, Key.LIST, Key.INTEGER), origin);
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
