package net.team33.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class FinalSet<E> implements Set<E> {

    private final Set<E> backing;

    private FinalSet(final Set<E> backing) {
        this.backing = unmodifiableSet(new LinkedHashSet<>(backing));
    }

    @Override
    public final int size() {
        return backing.size();
    }

    @Override
    public final boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public final boolean contains(final Object o) {
        return backing.contains(o);
    }

    @Override
    public final Iterator<E> iterator() {
        return new FinalIterator<>(backing.iterator());
    }

    @Override
    public final Object[] toArray() {
        return backing.toArray();
    }

    @Override
    public final <T> T[] toArray(final T[] a) {
        return backing.toArray(a);
    }

    @Override
    public final boolean add(final E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean containsAll(final Collection<?> c) {
        return backing.containsAll(c);
    }

    @Override
    public final boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    public static class Builder {
    }
}
