package net.team33.collections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class SmartTest {

    private static final EnumSet<Element> ELEMENTS = EnumSet.allOf(Element.class);

    @SuppressWarnings({"SuspiciousMethodCalls", "SetReplaceableByEnumSet"})
    @Test(expected = NullPointerException.class)
    public void directRemoveNullFromTreeSet() {
        final Collection<Element> subject = new TreeSet<>(ELEMENTS);
        subject.remove(null);
    }

    @SuppressWarnings("SetReplaceableByEnumSet")
    @Test
    public void smartRemoveNullFromTreeSet() {
        final Collection<Element> subject = new TreeSet<>(ELEMENTS);
        assertEquals(
                ELEMENTS,
                Smart.remove(subject, null)
        );
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "SetReplaceableByEnumSet"})
    @Test(expected = ClassCastException.class)
    public void directRemoveOtherFromTreeSet() {
        final Collection<Element> subject = new TreeSet<>(EnumSet.allOf(Element.class));
        subject.remove(new Date());
    }

    @SuppressWarnings("SetReplaceableByEnumSet")
    @Test
    public void smartRemoveOtherFromTreeSet() {
        final Collection<Element> subject = new TreeSet<>(ELEMENTS);
        assertEquals(
                ELEMENTS,
                Smart.remove(subject, new Date())
        );
    }

    @Test
    public void directRemoveRepeatedFromArrayList() {
        final Collection<Element> subject = new ArrayList<>(asList(
                Element.ABCD, Element.EFGH, Element.ABCD, Element.IJKL, Element.EFGH, Element.ABCD));
        subject.remove(Element.ABCD);
        assertEquals(
                asList(Element.EFGH, Element.ABCD, Element.IJKL, Element.EFGH, Element.ABCD),
                subject
        );
    }

    @Test
    public void smartRemoveRepeatedFromArrayList() {
        final Collection<Element> subject = new ArrayList<>(asList(
                Element.ABCD, Element.EFGH, Element.ABCD, Element.IJKL, Element.EFGH, Element.ABCD));
        assertEquals(
                asList(Element.EFGH, Element.IJKL, Element.EFGH),
                Smart.remove(subject, Element.ABCD)
        );
    }

    @Test
    public void directRemoveAllRepeatedFromArrayList() {
        final Collection<Element> subject = new ArrayList<>(asList(
                Element.ABCD, Element.EFGH, Element.ABCD, Element.IJKL, Element.EFGH, Element.ABCD));
        subject.removeAll(asList(Element.ABCD, Element.IJKL));
        assertEquals(
                asList(Element.EFGH, Element.EFGH),
                subject
        );
    }

    @SuppressWarnings("SpellCheckingInspection")
    private enum Element {
        ABCD, EFGH, IJKL
    }
}