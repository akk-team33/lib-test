package net.team33.test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Represents a report over (typically) multiple tests.
 *
 * Provides implementations of {@link #equals(Object)}, {@link #hashCode()} and {@link #toString()}
 * that are optimized for unit tests.
 */
public class Report {

    public static final Report EMPTY = new Report(emptyList());

    private static final String START_LIST = "[";
    private static final String END_LIST = "]";
    private static final String NEW_LINE = String.format("%n");
    private static final String INDENT = "   ";
    private static final String COMMA = ",";
    private static final String NULL = "<null>";

    private final List<Object> entries;

    public Report(final List<?> entries) throws NullPointerException {
        this.entries = unmodifiableList(new ArrayList<>(entries));
    }

    @Override
    public final boolean equals(final Object other) {
        return (this == other) || ((other instanceof Report) && equalsInner((Report) other));
    }

    @Override
    public final int hashCode() {
        return entries.hashCode();
    }

    @Override
    public final String toString() {
        return build(new StringBuilder(0), entries, 0).toString();
    }

    private static StringBuilder build(final StringBuilder result, final Object subject, final int indent) {
        if (null == subject)
            return result.append(NULL);
        else if (subject.getClass().isArray())
            return buildIterable(result, new ArrayIterator(subject), indent);
        else if (subject instanceof Map<?,?>)
            return buildIterable(result, ((Map<?,?>) subject).entrySet().iterator(), indent);
        else if (subject instanceof Iterable<?>)
            return buildIterable(result, ((Iterable<?>) subject).iterator(), indent);
        else if (subject instanceof Map.Entry<?,?>)
            return buildEntry(result, (Map.Entry<?,?>) subject, indent);
        else
            return result.append(subject.toString());
    }

    private static StringBuilder buildEntry(final StringBuilder result, final Map.Entry<?, ?> entry, final int indent) {
        build(result, entry.getKey(), indent);
        result.append(" -> ");
        return build(result, entry.getValue(), indent);
    }

    private static StringBuilder buildIterable(
            final StringBuilder result, final Iterator<?> iterator, final int indent0) {

        result.append(START_LIST);
        if (iterator.hasNext()) {
            final int indent = indent0 + 1;
            buildNext(result, iterator.next(), indent);
            while (iterator.hasNext()) {
                result.append(COMMA);
                buildNext(result, iterator.next(), indent);
            }
            buildNewLine(result, indent0);
        }
        return result.append(END_LIST);
    }

    private static void buildNext(final StringBuilder result, final Object next, final int indent) {
        buildNewLine(result, indent);
        build(result, next, indent);
    }

    private static void buildNewLine(final StringBuilder result, final int indent) {
        result.append(NEW_LINE);
        for (int i = 0; i < indent; ++i) {
            result.append(INDENT);
        }
    }

    private boolean equalsInner(final Report other) {
        return entries.equals(other.entries);
    }

    private static class ArrayIterator implements Iterator<Object> {

        private final Object array;
        private final int limit;

        private int index = 0;

        private ArrayIterator(final Object array) {
            this.array = array;
            this.limit = Array.getLength(array);
        }

        @Override
        public final boolean hasNext() {
            return index < limit;
        }

        @Override
        public final Object next() {
            if (hasNext()){
                final Object result = Array.get(array, index);
                index += 1;
                return result;

            } else {
                throw new NoSuchElementException(String.format("index(%d) >= limit(%d)", index, limit));
            }
        }

        @Override
        public final void remove() {
            throw new UnsupportedOperationException("not supported");
        }
    }
}
