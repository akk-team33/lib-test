package net.team33.collections;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableSet;

/**
 * Basic implementation of a {@link Builder} for immutable instances of {@link Set},
 * backed by a specific type of {@link Set}.
 *
 * @param <E> The element type
 * @param <B> The type of the final implementation
 */
@SuppressWarnings("ReturnOfThis")
public abstract class SetBuilder<E, B extends SetBuilder<E, B>> implements Builder<Set<E>> {

    private final Supplier<Set<E>> newSet;
    private final Set<E> backing;

    /**
     * Initiates a new instance by a {@link Supplier}.
     *
     * @param newSet Not {@code null}. Is expected to supply
     *               a new mutable empty set of the desired type any time it's {@link Supplier#get() applied}.
     */
    public SetBuilder(final Supplier<Set<E>> newSet) {
        this.newSet = newSet;
        backing = newSet.get();
    }

    private static <E> Set<E> copy(final Collection<? extends E> template, final Set<E> result) {
        result.addAll(template);
        return result;
    }

    @Override
    public final Set<E> build() {
        return unmodifiableSet(copy(backing, newSet.get()));
    }

    /**
     * @throws UnsupportedOperationException may occur only if the {@link #SetBuilder(Supplier) initial supplier}
     *                                       does not meet its requirement to supply a mutable set.
     * @throws ClassCastException            may occur only if used raw or forced in a mismatched class context.
     * @throws NullPointerException          if the specified element is {@code null}
     *                                       and the underlying set does not permit {@code null} elements.
     * @throws IllegalArgumentException      if some property of the specified element
     *                                       prevents it from being added to the underlying set.
     */
    public final B add(final E element) {
        backing.add(element);
        // <this> must be an instance of <B> ...
        // noinspection unchecked
        return (B) this;
    }

    /**
     * @throws UnsupportedOperationException may occur only if the {@link #SetBuilder(Supplier) initial supplier}
     *                                       does not meet its requirement to supply a mutable set.
     * @throws ClassCastException            may occur only if used raw or forced in a mismatched class context.
     * @throws NullPointerException          if the specified collection or any of its elements is {@code null}
     *                                       but the underlying set does not permit {@code null} elements.
     * @throws IllegalArgumentException      if some property of any of the specified elements
     *                                       prevents it from being added to the underlying set.
     */
    public final B addAll(final Collection<? extends E> elements) {
        backing.addAll(elements);
        // <this> must be an instance of <B> ...
        // noinspection unchecked
        return (B) this;
    }

    /**
     * @throws UnsupportedOperationException may occur only if the {@link #SetBuilder(Supplier) initial supplier}
     *                                       does not meet its requirement to supply a mutable set.
     */
    public final B remove(final E element) {
        Smart.remove(backing, element);
        try {
            backing.remove(element);
        } catch (@SuppressWarnings("ProhibitedExceptionCaught") final ClassCastException | NullPointerException ignored) {
            // Removing an element that cannot be contained is nothing else than
            // removing an element that simply is not contained
        }
        // <this> must be an instance of <B> ...
        // noinspection unchecked
        return (B) this;
    }

    /**
     * @throws UnsupportedOperationException may occur only if the {@link #SetBuilder(Supplier) initial supplier}
     *                                       does not meet its requirement to supply a mutable set.
     * @throws NullPointerException          if the specified collection is {@code null}.
     */
    public final B removeAll(final Collection<? extends E> elements) {
        try {
            backing.removeAll(elements);
        } catch (@SuppressWarnings("ProhibitedExceptionCaught") final ClassCastException | NullPointerException ignored) {
            // Removing elements that cannot be contained is nothing else
            // but removing elements that simply are not contained
            // But the process may be incomplete, so retry step-wise ...
            for (final E element : elements) {
                remove(element);
            }
        }
        // <this> must be an instance of <B> ...
        // noinspection unchecked
        return (B) this;
    }

    public final B clear() {
        // <this> must be an instance of <B> ...
        // noinspection unchecked
        return (B) this;
    }
}
