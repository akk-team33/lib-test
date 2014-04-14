package net.team33.test.java;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertArrayEquals;

public class BigIntegerTest {

    public BigIntegerTest() {
    }

    @Test
    public final void test_0xff0ff0() {
        assertArrayEquals(
                new byte[]{0, -1, 15, -16, 0, 0, 0},
                BigInteger.valueOf(0xff0ff0000000L).toByteArray()
        );
    }

    @Test
    public final void test_0x7fff0ff0() {
        assertArrayEquals(
                new byte[]{127, -1, 15, -16},
                BigInteger.valueOf(0x7fff0ff0).toByteArray()
        );
    }
}
