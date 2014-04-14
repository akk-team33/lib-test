package net.team33.test;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableSet;

/**
 * A utility to represent arbitrary objects to be comparable via {@link Object#equals(Object)}
 * (eg. within {@code org.junit.Assert#assertEquals(Object, Object)}) even if there is no appropriate implementation
 * of {@link Object#equals(Object)} in such an object for itself.
 * <p/>
 * In particular such an object will be represented as ...
 * <ul>
 * <li>... a {@link Map} or ...</li>
 * <li>... a {@link Set} or ...</li>
 * <li>... a {@link List} or ...</li>
 * <li>... as itself</li>
 * </ul>
 * <p/>
 * Further such an object-representation provides a multi-line {@linkplain #toString() String-representation} that
 * will make it more easy to view differences between two instances than a common {@linkplain #toString()
 * String-representations} would do.
 * <p/>
 * It is (preliminary) targeted to be used with (eg.) {@code org.junit.Assert#assertEquals(Object, Object)} and
 * currently has several limitations/restrictions.
 */
public class DataMapper {

    private static final String INDENTATION = "    ";
    private static final String NEWLINE = "\n";
    private static final String EMPTY_STRING = "";
    private static final String COMMA = ",";
    private static final String ITERABLE_INTRO = "[";
    private static final String ITERABLE_ENDING = "]";
    private static final String SEMICOLON = ";";
    private static final String ASSIGNMENT = " = ";
    private static final String MAP_INTRO = "{";
    private static final String MAP_ENDING = "}";
    private static final String STRING_LIMIT = "\"";
    private static final String ESC_STRING_LIMIT = "\\\"";
    private static final String BACKSLASH = "\\";
    private static final String ESC_BACKSLASH = "\\\\";
    private static final String OTHER_INTRO = "<";
    private static final String OTHER_ENDING = ">";
    private static final String NULL_STRING = "<null>";
    private static final Set<Class<?>> IRRESOLVABLE = unmodifiableSet(new HashSet<>(Arrays.<Class<?>>asList(
            Boolean.class,
            Character.class,
            Number.class,
            String.class,
            Date.class,
            Collection.class,
            Map.class,
            Class.class,
            Throwable.class)));
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private final Set<Class<?>> irresolvable;
    private final Set<String> ignorable;

    @SuppressWarnings("OverloadedVarargsMethod")
    public DataMapper(final Class<?>... irresolvable) {
        this(Arrays.asList(irresolvable));
    }

    public DataMapper(final Collection<? extends Class<?>> irresolvable) {
        //noinspection CollectionsFieldAccessReplaceableByMethodCall,unchecked
        this(irresolvable, Collections.EMPTY_SET);
    }


    public DataMapper(final Collection<? extends Class<?>> irresolvable,
                      final Collection<? extends String> ignorable) {
        this.irresolvable = unmodifiableSet(new HashSet<>(irresolvable));
        this.ignorable = unmodifiableSet(new HashSet<>(ignorable));
    }

    @SuppressWarnings({"MethodWithMultipleReturnPoints", "IfStatementWithTooManyBranches"})
    public Object map(final Object subject) throws IllegalAccessException {
        return mappingFor(subject).map(subject, this);
    }

    public DataMapper ignore(final Collection<? extends String> ignorable) {
        return new DataMapper(irresolvable, ignorable);
    }

    private static boolean isIrresolvable(final Class<?> subject, final Iterable<Class<?>> irresolvable) {
        return (null == subject)
                || Object.class.equals(subject)
                || subject.isPrimitive()
                || subject.isEnum()
                || subject.isArray()
                || containsAssignableFrom(IRRESOLVABLE, subject)
                || containsAssignableFrom(irresolvable, subject);
    }

    private static boolean containsAssignableFrom(final Iterable<Class<?>> classes, final Class<?> subject) {
        for (final Class<?> entry : classes) {
            if (entry.isAssignableFrom(subject)) {
                return true;
            }
        }
        return false;
    }

    private Mapping mappingFor(final Object subject) {
        return null == subject ? Mapping.DIRECT : Mapping.instanceFor(subject.getClass(), this);
    }

    private DataMap map(final DataMap result, final Map<?, ?> subject) throws IllegalAccessException {
        for (final Map.Entry<?, ?> item : subject.entrySet()) {
            result.put(map(item.getKey()), map(item.getValue()));
        }
        return result;
    }

    private <C extends Collection<Object>> C map(final C result,
                                                 final Iterable<?> subject) throws IllegalAccessException {
        for (final Object item : subject) {
            result.add(map(item));
        }
        return result;
    }

    private DataList mapArray(final DataList result, final Object array) throws IllegalAccessException {
        for (int index = 0, max = Array.getLength(array); index < max; ++index) {
            result.add(map(Array.get(array, index)));
        }
        return result;
    }

    private static boolean isRelevant(final int modifiers) {
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    private DataMap map(final DataMap result, final Class<?> subjectClass, final Object subject)
            throws IllegalAccessException {

        final Class<?> superclass = subjectClass.getSuperclass();
        if (null != superclass && !Object.class.equals(superclass)) {
            map(result, superclass, subject);
        }

        for (final Field declaredField : subjectClass.getDeclaredFields()) {
            if (isRelevant(declaredField.getModifiers())) {
                declaredField.setAccessible(true);
                try {
                    if (isRelevant(declaredField.getModifiers())) {
                        final String key = subjectClass.getCanonicalName() + "." + declaredField.getName();
                        if (!ignorable.contains(key)) {
                            result.put(key, map(declaredField.get(subject)));
                        }
                    }
                } finally {
                    declaredField.setAccessible(false);
                }
            }
        }
        return result;
    }

    private boolean isResolvable(final Class<?> subjectClass) {
        return !isIrresolvable(subjectClass, irresolvable);
    }

    private static StringBuilder buildFromMap(final StringBuilder result, final Map<?, ?> subject, final int level0) {
        result.append(MAP_INTRO);
        final Iterator<? extends Map.Entry<?, ?>> iterator = subject.entrySet().iterator();
        if (iterator.hasNext()) {
            final int level = level0 + 1;
            while (iterator.hasNext()) {
                buildFromEntry(result, iterator.next(), level);
            }
            newLine(result, level0);
        }
        result.append(MAP_ENDING);
        return result;
    }

    private static void buildFromEntry(final StringBuilder result, final Map.Entry<?, ?> next, final int level) {
        newLine(result, level);
        result.append(next.getKey());
        result.append(ASSIGNMENT);
        build(result, next.getValue(), level);
        result.append(SEMICOLON);
    }

    private static StringBuilder buildFromIterable(final StringBuilder result, final Iterable<?> subject,
                                                   final int level0) {
        result.append(ITERABLE_INTRO);
        final Iterator<?> iterator = subject.iterator();
        if (iterator.hasNext()) {
            final int level = level0 + 1;
            String appendix = EMPTY_STRING;
            while (iterator.hasNext()) {
                result.append(appendix);
                appendix = COMMA;
                newLine(result, level);
                final Object next = iterator.next();
                build(result, next, level);
            }
            newLine(result, level0);
        }
        result.append(ITERABLE_ENDING);
        return result;
    }

    @SuppressWarnings({"OverloadedMethodsWithSameNumberOfParameters", "IfStatementWithTooManyBranches"})
    private static StringBuilder build(final StringBuilder result, final Object subject, final int level) {
        if (Map.class.isInstance(subject)) {
            buildFromMap(result, Map.class.cast(subject), level);

        } else if (Iterable.class.isInstance(subject)) {
            buildFromIterable(result, Iterable.class.cast(subject), level);

        } else if (Number.class.isInstance(subject)) {
            result.append(subject);

        } else if (CharSequence.class.isInstance(subject)) {
            result.append(STRING_LIMIT);
            result.append(subject.toString().replace(BACKSLASH, ESC_BACKSLASH).replace(STRING_LIMIT, ESC_STRING_LIMIT));
            result.append(STRING_LIMIT);

        } else if (null != subject) {
            result.append(OTHER_INTRO);
            result.append(subject);
            result.append(OTHER_ENDING);

        } else {
            result.append(NULL_STRING);
        }
        return result;
    }

    private static void newLine(final StringBuilder result, final int level) {
        result.append(NEWLINE);
        for (int indent = 0; indent < level; ++indent) {
            result.append(INDENTATION);
        }
    }

    /**
     * CAUTION: order is essential! - TODO?
     */
    private enum Mapping {
        FOR_ARRAYS {
            @Override
            Object map(final Object subject, final DataMapper context) throws IllegalAccessException {
                return context.mapArray(new DataList(), subject);
            }

            @Override
            boolean isApplicableFor(final Class<?> subject, final DataMapper context) {
                return subject.isArray();
            }
        },
        FOR_MAPS {
            @Override
            Object map(final Object subject, final DataMapper context) throws IllegalAccessException {
                return context.map(new DataMap(), Map.class.cast(subject));
            }

            @Override
            boolean isApplicableFor(final Class<?> subject, final DataMapper context) {
                return Map.class.isAssignableFrom(subject);
            }
        },
        FOR_SETS {
            @Override
            Object map(final Object subject, final DataMapper context) throws IllegalAccessException {
                return context.map(new DataSet(), Set.class.cast(subject));
            }

            @Override
            boolean isApplicableFor(final Class<?> subject, final DataMapper context) {
                return Set.class.isAssignableFrom(subject);
            }
        },
        FOR_COLLECTIONS {
            @Override
            Object map(final Object subject, final DataMapper context) throws IllegalAccessException {
                return context.map(new DataList(), Collection.class.cast(subject));
            }

            @Override
            boolean isApplicableFor(final Class<?> subject, final DataMapper context) {
                return Collection.class.isAssignableFrom(subject);
            }
        },
        FOR_DATA_OBJECTS {
            @Override
            Object map(final Object subject, final DataMapper context) throws IllegalAccessException {
                return context.map(new DataMap(), subject.getClass(), subject);
            }

            @Override
            boolean isApplicableFor(final Class<?> subject, final DataMapper context) {
                return context.isResolvable(subject);
            }
        },
        DIRECT {
            @Override
            Object map(final Object subject, final DataMapper context) {
                return subject;
            }

            @Override
            boolean isApplicableFor(final Class<?> subject, final DataMapper context) {
                return true;
            }
        };

        public static Mapping instanceFor(final Class<?> subject, final DataMapper context) {
            for (final Mapping value : values()) {
                if (value.isApplicableFor(subject, context)) {
                    return value;
                }
            }
            //noinspection ProhibitedExceptionThrown
            throw new Error(String.format("No mapping found for <%s> in context <%s>", subject, context));
        }

        abstract Object map(final Object subject, final DataMapper context) throws IllegalAccessException;

        abstract boolean isApplicableFor(final Class<?> subject, final DataMapper context);
    }

    @SuppressWarnings({"CloneableClassWithoutClone", "ClassExtendsConcreteCollection", "CloneableClassInSecureContext"})
    private static class DataMap extends TreeMap<Object, Object> {

        @Override
        public final String toString() {
            return buildFromMap(new StringBuilder(0), this, 0).toString();
        }
    }

    @SuppressWarnings({"CloneableClassWithoutClone", "ClassExtendsConcreteCollection", "CloneableClassInSecureContext"})
    private static class DataList extends ArrayList<Object> {

        @Override
        public final String toString() {
            return buildFromIterable(new StringBuilder(0), this, 0).toString();
        }
    }

    @SuppressWarnings({"CloneableClassWithoutClone", "ClassExtendsConcreteCollection", "CloneableClassInSecureContext"})
    private static class DataSet extends LinkedHashSet<Object> {

        @Override
        public final String toString() {
            return buildFromIterable(new StringBuilder(0), this, 0).toString();
        }
    }
}
