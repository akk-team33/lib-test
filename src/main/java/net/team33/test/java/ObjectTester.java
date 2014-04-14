package net.team33.test.java;

import java.util.List;

public final class ObjectTester {

    private static final String FORMAT_EQUALS_REFLEXIVE = "Must be reflexive: for any non-null reference value x(%s), x.equals(x) should return true.";

    private ObjectTester() {
    }

    public static List<Object> testEquals(
            final List<Object> result, final Object subject1, final Object subject2, final Object subject3) {
        testEqualsReflexive(result, subject1);
        return result;
    }

    public static List<Object> testEqualsReflexive(final List<Object> result, final Object subject) {
        if(null != subject) {
            if (not(subject.equals(subject))) {
                result.add(String.format(FORMAT_EQUALS_REFLEXIVE, subject));
            }
        }
        return result;
    }

    private static boolean not(final boolean value) {
        return !value;
    }
}
