package net.team33.collections;

import java.util.Collection;
import java.util.Iterator;

public final class Smart {
    private Smart() {
    }

    /**
     * Removes a specified {@code element} from a {@code subject}, if it does contain that {@code element}.
     * <p/>
     * If {@code subject} contains the specified {@code element} several times, each of those will be removed.
     * <p/>
     * If {@code subject} doesn't contain the specified {@code element}, the collection remains unmodified,
     * regardless of weather or not it can contain the {@code element} at all.
     * <p/>
     * If {@code subject} is {@code null}, it is finally treated like an empty collection
     *
     * @return The {@code subject}, may be {@code null}.
     */
    @SuppressWarnings({"ProhibitedExceptionCaught", "StatementWithEmptyBody", "SuspiciousMethodCalls"})
    public static <E, C extends Collection<E>> C remove(final C subject, final Object element) {
        try {
            while (subject.remove(element)) {
            }
        } catch (final ClassCastException | NullPointerException ignored) {
            // --> <subject> cannot contain <element>
            // --> same as <subject> simply does not contain <element>
            // --> nothing to do
        }
        return subject;
    }

    public static <E, C extends Collection<E>> C removeAll(final C subject, final Collection<?> elements) {
        Iterator<?> it = subject.iterator();
        while (it.hasNext()) {
            if (elements.contains(it.next())) {
                it.remove();
            }
        }
        try {
            subject.removeAll(elements);
        } catch (final ClassCastException | NullPointerException ignored) {
            // --> <subject> cannot contain some of <elements>
            // --> same as <subject> simply does not contain some of <elements>
            // --> nothing to do
        }
        return subject;
    }
}
