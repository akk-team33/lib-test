package net.team33.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class CombIterator<K, V> implements Iterator<Map<K, V>> {

    private final Map<? extends K, ? extends Iterable<? extends V>> origin;
    private final Map<K, Iterator<? extends V>> iterators;
    private final HashMap<K, V> template;

    /**
     * @param origin An original map containing all intended values for each intended key.
     * @throws NullPointerException when the original map or any of its values is {@code null}.
     */
    public CombIterator(final Map<? extends K, ? extends Iterable<? extends V>> origin)
            throws NullPointerException, IllegalArgumentException {

        // Maps the keys to their relating iterators and prepares a template for the next result ...
        this.iterators = new HashMap<>(origin.size());
        this.template = new LinkedHashMap<>(origin.size());
        for (final K key : origin.keySet()) {
            resume(key, origin.get(key).iterator());
        }

        // To be able to resume the iterators ...
        this.origin = origin;
    }

    @Override
    public final boolean hasNext() {
        return (0 < origin.size()) && (template.size() == origin.size());
    }

    @Override
    public final Map<K, V> next() throws NoSuchElementException {
        if (hasNext()) {
            final Map<K, V> result = new LinkedHashMap<>(template);
            update(origin.keySet().iterator());
            return result;

        } else {
            throw new NoSuchElementException("There is no next element available");
        }
    }

    private void update(final Iterator<? extends K> keys) {
        update(keys.next(), keys);
    }

    private void update(final K key, final Iterator<? extends K> moreKeys) {
        update(key, iterators.get(key), moreKeys);
    }

    private void update(final K key, final Iterator<? extends V> values, final Iterator<? extends K> moreKeys) {
        if (values.hasNext()) {
            template.put(key, values.next());

        } else if (moreKeys.hasNext()) {
            resume(key, origin.get(key).iterator());
            update(moreKeys.next(), moreKeys);

        } else {
            template.clear();
            // -> hasNext() -> false
        }
    }

    private void resume(final K key, final Iterator<? extends V> iterator) {
        iterators.put(key, iterator);
        if (iterator.hasNext()) {
            template.put(key, iterator.next());
        } else if (template.containsKey(key)) {
            template.remove(key);
        }
    }
}
