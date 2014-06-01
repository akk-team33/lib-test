package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

@SuppressWarnings("unchecked")
public class CombIteratorTest {

    private static final Map<Integer, List<Integer>> ORIGIN_00;
    private static final Map<Integer, List<Integer>> ORIGIN_01;
    private static final Map<Integer, List<Integer>> ORIGIN_02;
    private static final Map<Integer, List<Integer>> ORIGIN_03;

    static {
        final Map<Integer, List<Integer>> origin01 = new TreeMap<>();
        origin01.put(1, asList(1, 2, 3));
        origin01.put(2, asList(1, 2, 3));
        origin01.put(3, asList(1, 2, 3));

        final Map<Integer, List<Integer>> origin02 = new TreeMap<>();
        origin02.put(1, asList(1, 2, 3));
        origin02.put(2, asList());
        origin02.put(3, asList(1, 2, 3));

        final Map<Integer, List<Integer>> origin03 = new TreeMap<>();
        origin03.put(1, asList(1, 2, 3));
        origin03.put(2, null);
        origin03.put(3, asList(1, 2, 3));

        ORIGIN_00 = emptyMap();
        ORIGIN_01 = unmodifiableMap(origin01);
        ORIGIN_02 = unmodifiableMap(origin02);
        ORIGIN_03 = unmodifiableMap(origin03);
    }

    private static final Reporter.Tester<Map<Integer, Integer>> TESTER = (context, subject) -> context.report(subject);

    private static Map.Entry entry(final Object key, final Object value) {
        return new SimpleEntry(key, value);
    }

    private static Map map(final Map.Entry... entries) {
        final Map result = new LinkedHashMap();
        for (final Map.Entry entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Test(expected = NoSuchElementException.class)
    public void testNoMoreElement() throws Exception {
        final CombIterator<Integer, Integer> subject = new CombIterator<>(ORIGIN_01);
        while (subject.hasNext()) {
            subject.next();
        }
        // should cause the expectation ...
        subject.next();
    }

    @Test
    public void testOriginIsNormal() {
        Assert.assertEquals(
                new Report(asList(
                        map(entry(1, 1), entry(2, 1), entry(3, 1)),
                        map(entry(1, 2), entry(2, 1), entry(3, 1)),
                        map(entry(1, 3), entry(2, 1), entry(3, 1)),
                        map(entry(1, 1), entry(2, 2), entry(3, 1)),
                        map(entry(1, 2), entry(2, 2), entry(3, 1)),
                        map(entry(1, 3), entry(2, 2), entry(3, 1)),
                        map(entry(1, 1), entry(2, 3), entry(3, 1)),
                        map(entry(1, 2), entry(2, 3), entry(3, 1)),
                        map(entry(1, 3), entry(2, 3), entry(3, 1)),
                        map(entry(1, 1), entry(2, 1), entry(3, 2)),
                        map(entry(1, 2), entry(2, 1), entry(3, 2)),
                        map(entry(1, 3), entry(2, 1), entry(3, 2)),
                        map(entry(1, 1), entry(2, 2), entry(3, 2)),
                        map(entry(1, 2), entry(2, 2), entry(3, 2)),
                        map(entry(1, 3), entry(2, 2), entry(3, 2)),
                        map(entry(1, 1), entry(2, 3), entry(3, 2)),
                        map(entry(1, 2), entry(2, 3), entry(3, 2)),
                        map(entry(1, 3), entry(2, 3), entry(3, 2)),
                        map(entry(1, 1), entry(2, 1), entry(3, 3)),
                        map(entry(1, 2), entry(2, 1), entry(3, 3)),
                        map(entry(1, 3), entry(2, 1), entry(3, 3)),
                        map(entry(1, 1), entry(2, 2), entry(3, 3)),
                        map(entry(1, 2), entry(2, 2), entry(3, 3)),
                        map(entry(1, 3), entry(2, 2), entry(3, 3)),
                        map(entry(1, 1), entry(2, 3), entry(3, 3)),
                        map(entry(1, 2), entry(2, 3), entry(3, 3)),
                        map(entry(1, 3), entry(2, 3), entry(3, 3))
                )),
                Reporter.test(new CombIterator<>(ORIGIN_01), TESTER)
        );
    }

    @Test
    public void testOriginContainsEmpty() {
        Assert.assertEquals(
                Report.EMPTY,
                Reporter.test(new CombIterator<>(ORIGIN_02), TESTER)
        );
    }

    @Test
    public void testOriginIsEmpty() {
        Assert.assertEquals(
                Report.EMPTY,
                Reporter.test(new CombIterator<>(ORIGIN_00), TESTER)
        );
    }

    @Test(expected = NullPointerException.class)
    public void testOriginIsNull() {
        new CombIterator<>(null);
    }

    @Test(expected = NullPointerException.class)
    public void testOriginContainsNull() {
        new CombIterator<>(ORIGIN_03);
    }
}
