package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import static java.util.Collections.singleton;

public class NormalizerTest {

    private static final String EXPECTED = "expected";
    private static final String BUT_WAS = "but was";

    @Test
    public void testNormal() throws Exception {
        Assert.assertEquals(
                Report.EMPTY,
                Reporter.test(singleton(new Normalizer()), new NormalTester(new Object(), new Object()))
        );
    }

    private static class NormalTester implements Reporter.Tester<Normalizer> {

        private final Object expectation;
        private final Object result;

        private NormalTester(final Object expectation, final Object result) {
            this.expectation = expectation;
            this.result = result;
        }

        @Override
        public void test(final Reporter context, final Normalizer subject) {
            context.expectEquals(
                    subject.normal(expectation),
                    subject.normal(result),
                    null
//                    Mapper.<String, Object>instance(LinkedHashMap::new)
//                            .put(EXPECTED, subject.normal(expectation))
//                            .put(BUT_WAS, subject.normal(result))
//                            .build()
            );
        }
    }
}