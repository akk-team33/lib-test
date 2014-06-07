package net.team33.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Reporter {

    private final List<Object> report = new LinkedList<>();

    public static <S> Report test(final Iterable<S> subjects, final Tester<? super S> tester) {
        return test(subjects.iterator(), tester);
    }

    public static <S> Report test(final Iterator<S> subjects, final Tester<? super S> tester) {
        final Reporter context = new Reporter();
        while (subjects.hasNext()) {
            tester.test(context, subjects.next());
        }
        return new Report(context.report);
    }

    public final Reporter report(final Object entry) {
        report.add(entry);
        return this;
    }

    public final boolean expectTrue(final boolean subject, final Object elseToBeReported) {
        return subject || report(elseToBeReported).than(false);
    }

    public final boolean expectFalse(final boolean subject, final Object elseToBeReported) {
        return !subject || report(elseToBeReported).than(false);
    }

    public final boolean expectEquals(final Object expected, final Object subject, final Object elseToBeReported) {
        return expectTrue(Objects.equals(expected, subject), elseToBeReported);
    }

    public final boolean expectNotEquals(final Object expected, final Object subject, final Object elseToBeReported) {
        return expectFalse(Objects.equals(expected, subject), elseToBeReported);
    }

    private boolean than(final boolean result) {
        return result;
    }

    @FunctionalInterface
    public interface Tester<S> {
        void test(final Reporter context, final S subject);
    }
}
