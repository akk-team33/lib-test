package net.team33.test;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Dispatcher {

    public static <P> List<Object> test(final Iterable<P> parameters, final Consumer<? super P> tester) {
        final List<Object> report = new LinkedList<>();
        for (final P parameter: parameters) {
            try {
                tester.accept(parameter);
            } catch (final AssertionError e) {
                report.add(e);
            }
        }
        return report;
    }
}
