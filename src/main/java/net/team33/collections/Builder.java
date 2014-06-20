package net.team33.collections;

/**
 * Abstracts a kind of factory that may be filled step-wise with construction parameters (e.g. properties of an instance
 * to be built) to finally {@link #build()} an instance of a specific type.
 */
public interface Builder<T> {

    /**
     * Creates a new instance of the underlying type.
     *
     * @return Not {@code null}!
     */
    T build();
}
