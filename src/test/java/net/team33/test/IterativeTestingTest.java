package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class IterativeTestingTest {

    private static final int TEST_FAIL_SIZE = 15;

    @Test
    public final void testFail() {
        Assert.assertEquals(
                "There should be a Fail for each Parameter",
                TEST_FAIL_SIZE,
                Dispatcher.test(parameters(TEST_FAIL_SIZE), IterativeTestingTest::testFail).size());
    }

    private static void testFail(final Parameter parameter) {
        Assert.fail("parameter(" + parameter + ")");
    }

    private Collection<Parameter> parameters(final int size) {
        final Collection<Parameter> result = new ArrayList<>(size);
        for (int i = 0; i < size; ++i) {
            result.add(new Parameter(i));
        }
        return result;
    }

    private static class Parameter {
        private final int index;

        public Parameter(final int index) {
            this.index = index;
        }
    }
}
