package net.team33.test;

import java.util.Iterator;

public class FinalIterator<E> implements Iterator<E> {
    private final Iterator<E> backing;

    public FinalIterator(final Iterator<E> backing) {
        this.backing = backing;
    }

    @Override
    public final boolean hasNext() {
        return backing.hasNext();
    }

    @Override
    public final E next() {
        return backing.next();
    }
}
