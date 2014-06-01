package net.team33.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ReporterTest {

    private static final String EXPECTED_FALSE_BUT_WAS_TRUE = "expected <false> but was <true>";
    private static final String EXPECTED_TRUE_BUT_WAS_FALSE = "expected <true> but was <false>";
    private static final String EXPECTED_X_BUT_WAS_Y = "expected <%s> but was <%s>";
    private static final String UNEXPECTED_BUT_WAS_X = "unexpected <%s> but was <%s>";
    private static final String A_STRING = "a string";
    private static final Date A_DATE = new Date();

    @Test
    public void testExpectFalse() throws Exception {
        assertEquals(
                new Report(asList(
                        EXPECTED_FALSE_BUT_WAS_TRUE,
                        EXPECTED_FALSE_BUT_WAS_TRUE)),
                Reporter.test(asList(true, true, false), new ExpectFalseTester())
        );
    }

    @Test
    public void testExpectTrue() throws Exception {
        assertEquals(
                new Report(asList(
                        EXPECTED_TRUE_BUT_WAS_FALSE,
                        EXPECTED_TRUE_BUT_WAS_FALSE)),
                Reporter.test(asList(false, true, false), new ExpectTrueTester())
        );
    }

    @Test
    public void testExpectEquals() throws Exception {
        assertEquals(
                new Report(asList(
                        String.format(EXPECTED_X_BUT_WAS_Y, 2, 1),
                        String.format(EXPECTED_X_BUT_WAS_Y, 2, 3),
                        String.format(EXPECTED_X_BUT_WAS_Y, 2, 4))),
                Reporter.test(asList(1, 2, 3, 4), new ExpectEqualsTester(2))
        );
    }

    @Test
    public void testExpectNotEquals() throws Exception {
        assertEquals(
                new Report(asList(
                        String.format(UNEXPECTED_BUT_WAS_X, 2, 2))),
                Reporter.test(asList(1, 2, 3, 4), new ExpectNotEqualsTester(2))
        );
    }

    @Test
    public void testReport() throws Exception {
        final List<?> entries = asList(278, A_STRING, A_DATE, null, new ArrayList(0));
        assertEquals(
                new Report(entries),
                Reporter.test(entries, new ReportTester())
        );
    }

    private static class ExpectTrueTester implements Reporter.Tester<Boolean> {

        @Override
        public void test(final Reporter context, final Boolean subject) {
            context.expectTrue(subject, EXPECTED_TRUE_BUT_WAS_FALSE);
        }
    }

    private static class ExpectFalseTester implements Reporter.Tester<Boolean> {

        @Override
        public void test(final Reporter context, final Boolean subject) {
            context.expectFalse(subject, EXPECTED_FALSE_BUT_WAS_TRUE);
        }
    }

    private static class ExpectEqualsTester implements Reporter.Tester<Object> {
        private final Integer expected;

        private ExpectEqualsTester(final Integer expected) {this.expected = expected;}

        @Override
        public void test(final Reporter context, final Object subject) {
            context.expectEquals(expected, subject, String.format(EXPECTED_X_BUT_WAS_Y, expected, subject));
        }
    }

    private static class ExpectNotEqualsTester implements Reporter.Tester<Object> {
        private final Integer unexpected;

        private ExpectNotEqualsTester(final Integer unexpected) {this.unexpected = unexpected;}

        @Override
        public void test(final Reporter context, final Object subject) {
            context.expectNotEquals(unexpected, subject, String.format(UNEXPECTED_BUT_WAS_X, unexpected, subject));
        }
    }

    private static class ReportTester implements Reporter.Tester<Object> {
        @Override
        public void test(final Reporter context, final Object subject) {
            context.report(subject);
        }
    }
}
