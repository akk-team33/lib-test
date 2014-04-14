package net.team33.test.java;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test class for {@link ObjectTester}.
 */
public class ObjectTesterTest {
    public ObjectTesterTest() {
    }

    @Test
    public final void testTestEquals_null_null_null() throws Exception {
        Assert.assertEquals(
                Collections.emptyList(),
                Tester.testEquals(null, null, null)
        );
    }

    @Test
    public final void testTestEquals_1_1_1() throws Exception {
        Assert.assertEquals(
                Collections.emptyList(),
                Tester.testEquals(1, 1, 1)
        );
    }

    @Test
    public final void testTestEquals_1_2_1() throws Exception {
        Assert.assertEquals(
                Collections.emptyList(),
                Tester.testEquals(1, 2, 1)
        );
    }

    @Test
    public final void testTestEquals_1_1_2() throws Exception {
        Assert.assertEquals(
                Collections.emptyList(),
                Tester.testEquals(1, 1, 2)
        );
    }

    @Test
    public final void testTestEquals_1_2_2() throws Exception {
        Assert.assertEquals(
                Collections.emptyList(),
                Tester.testEquals(1, 2, 2)
        );
    }

    @Test
    public final void testTestEquals_1_2_3() throws Exception {
        Assert.assertEquals(
                Collections.emptyList(),
                Tester.testEquals(1, 2, 3)
        );
    }

    private static final class Tester {
        private Tester() {
        }

        public static List<Object> testEquals(final Object subject1, final Object subject2, final Object subject3) {
            return ObjectTester.testEquals(new ArrayList<>(0), subject1, subject2, subject3);
        }
    }
}
