package net.team33.test;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSortedSet;
import static java.util.Objects.requireNonNull;

/**
 * Provides multi lined textual representations for aggregate types
 */
public class Textual {

    private static final String ITERATION_START = "[";
    private static final String ITERATION_END = "]";
    private static final String NEW_LINE = "\n";
    private static final String INDENT = "    ";
    private static final String COMMA = ",";
    private static final String NULL_STRING = "null";
    private static final String STRING_START = "\"";
    private static final String STRING_Q_START = "\\\"";
    private static final String STRING_ESC = "\\";
    private static final String STRING_Q_ESC = "\\\\";
    private static final String OTHER_START = "<";
    private static final String OTHER_END = ">";

    private static void newLine(final int indent, final StringBuilder target) {
        target.append(NEW_LINE);
        for (int i = 0; i < indent; ++i) {
            target.append(INDENT);
        }
    }

    public final String valueOf(final Object subject) {
        final StringBuilder target = new StringBuilder(0);
        build(subject, 0, target);
        return target.toString();
    }

    private void build(final Object subject, final int indent, final StringBuilder target) {
        Type.valueOf(subject).build(this, subject, indent, target);
    }

    private void build(final Iterable<?> subject, final int indent, final StringBuilder target) {
        build(subject.iterator(), indent, target);
    }

    private void build(final Iterator<?> iterator, final int indent0, final StringBuilder target) {
        target.append(ITERATION_START);
        if (iterator.hasNext()) {
            final int indent = indent0 + 1;
            buildNext(iterator, indent, target);
            while (iterator.hasNext()) {
                target.append(COMMA);
                buildNext(iterator, indent, target);
            }
            newLine(indent0, target);
        }
        target.append(ITERATION_END);
    }

    private void buildNext(final Iterator<?> iterator, final int indent, final StringBuilder target) {
        newLine(indent, target);
        build(iterator.next(), indent, target);
    }

    private enum Priority {

        HIGH(0),
        NORMAL(1),
        LOW(2);
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        private final int level;

        Priority(final int level) {
            this.level = level;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    private enum Type {

        NULL(Priority.HIGH, subject -> null == subject) {
            @Override
            void build(final Textual context, final Object subject, final int indent, final StringBuilder target) {
                target.append(Textual.NULL_STRING);
            }
        },

        ITERABLE(Priority.NORMAL, Iterable.class::isInstance) {
            @Override
            void build(final Textual context, final Object subject, final int indent, final StringBuilder target) {
                context.build(Iterable.class.cast(subject), indent, target);
            }
        },

        SIMPLE(Priority.NORMAL, subject -> Number.class.isInstance(subject) || Boolean.class.isInstance(subject)) {
            @Override
            void build(final Textual context, final Object subject, final int indent, final StringBuilder target) {
                target.append(subject.toString());
            }
        },

        CHAR(Priority.NORMAL, Character.class::isInstance) {
            @Override
            void build(final Textual context, final Object subject, final int indent, final StringBuilder target) {
                target.append("'");
                target.append(subject.toString()
                        .replace(STRING_ESC, STRING_Q_ESC)
                        .replace("'", "\\\'"));
                target.append("'");
            }
        },

        TEXT(Priority.NORMAL, CharSequence.class::isInstance) {
            @Override
            void build(final Textual context, final Object subject, final int indent, final StringBuilder target) {
                target.append(STRING_START);
                target.append(subject.toString()
                        .replace(STRING_ESC, STRING_Q_ESC)
                        .replace(STRING_START, STRING_Q_START));
                target.append(STRING_START);
            }
        },

        OTHER(Priority.LOW, subject -> true) {
            @Override
            void build(final Textual context, final Object subject, final int indent, final StringBuilder target) {
                target.append(OTHER_START);
                TEXT.build(context, subject.toString(), indent, target);
                target.append(OTHER_END);
            }
        };

        private static final SortedSet<Type> SORTED = unmodifiableSortedSet(newSorted());
        private static final String NO_TYPE_MATCHING = "No Type is matching <%s> (%s)";
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        private final Priority priority;
        private final Predicate<Object> predicate;

        Type(final Priority priority, final Predicate<Object> predicate) {
            this.priority = requireNonNull(priority);
            this.predicate = requireNonNull(predicate);
        }

        private static SortedSet<Type> newSorted() {
            //noinspection SetReplaceableByEnumSet
            return EnumSet.allOf(Type.class).stream()
                    .collect(Collectors.toCollection(() -> new TreeSet<>(Order.INSTANCE)));
        }

        private static Type valueOf(final Object subject) {
            //noinspection AccessingNonPublicFieldOfAnotherObject
            return SORTED.stream()
                    .filter(type -> type.predicate.test(subject))
                    .findFirst()
                    .orElseThrow(() -> new Failure(NO_TYPE_MATCHING, subject, subject.getClass()));
        }

        abstract void build(Textual context, Object subject, int indent, StringBuilder target);

        private static class Failure extends Error {
            private Failure(final String format, final Object... args) {
                super(String.format(format, args));
            }
        }

        private static class Builder {}
    }

    @SuppressWarnings({"ComparatorNotSerializable", "AccessingNonPublicFieldOfAnotherObject"})
    private static class Order implements Comparator<Type> {
        public static final Order INSTANCE = new Order();

        private static int compare(final Priority left, final Priority right) {
            return Integer.compare(left.level, right.level);
        }

        @Override
        public final int compare(final Type left, final Type right) {
            final int result = compare(left.priority, right.priority);
            return (0 == result) ? left.compareTo(right) : result;
        }
    }
}
