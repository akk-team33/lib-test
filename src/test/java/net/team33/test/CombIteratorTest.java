package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.AbstractMap.SimpleEntry;

@SuppressWarnings("unchecked")
public class CombIteratorTest {

    private static final Map<Integer, List<Integer>> EMPTY_MAP = Collections.emptyMap();
    private static final Map<Integer, List<Integer>> WITH_NULL = map(
            entry(1, Arrays.asList(1, 2, 3)),
            entry(2, null),
            entry(3, Arrays.asList(1, 2, 3))
    );
    private static final Map<Integer, List<Integer>> WITH_EMPTY = map(
            entry(1, Arrays.asList(1, 2, 3)),
            entry(2, Collections.emptyList()),
            entry(3, Arrays.asList(1, 2, 3))
    );
    private static final Map<Integer, List<Integer>> ORIGIN = map(
            entry(1, Arrays.asList(1, 2, 3)),
            entry(2, Arrays.asList(1, 2, 3)),
            entry(3, Arrays.asList(1, 2, 3))
    );

    private static final List<Map<Integer, Integer>> EMPTY_LIST = Collections.emptyList();
    private static final List<Map<Integer, Integer>> EXPECTATION = Arrays.asList(
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
    );

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
    public void testHasNext() throws Exception {
        final CombIterator<Integer, Integer> subject = new CombIterator<>(ORIGIN);
        while (subject.hasNext()) {
            subject.next();
        }
        subject.next();
    }

    @Test
    public void testNext() {
        final List<Map<Integer, Integer>> result = new ArrayList<>(0);
        final CombIterator<Integer, Integer> subject = new CombIterator<>(ORIGIN);
        while (subject.hasNext()) {
            result.add(subject.next());
        }
        Assert.assertEquals(EXPECTATION, result);
    }

    @Test
    public void testMissing() {
        final List<Map<Integer, Integer>> result = new ArrayList<>(0);
        final CombIterator<Integer, Integer> subject = new CombIterator<>(WITH_EMPTY);
        while (subject.hasNext()) {
            result.add(subject.next());
        }
        Assert.assertEquals(EMPTY_LIST, result);
    }

    @Test
    public void testEmpty() {
        final List<Map<Integer, Integer>> result = new ArrayList<>(0);
        final CombIterator<Integer, Integer> subject = new CombIterator<>(EMPTY_MAP);
        while (subject.hasNext()) {
            result.add(subject.next());
        }
        Assert.assertEquals(EMPTY_LIST, result);
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        new CombIterator<>(null);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNull() {
        new CombIterator<>(WITH_NULL);
    }
}
