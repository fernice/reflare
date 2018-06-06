package de.krall.reflare.std;

import java.util.ArrayList;
import java.util.List;

/**
 * A highly specialized, array-like, sort-of list that aggressively tries to take up the least
 * amount of space possible.
 *
 * @param <E> the elements type
 */
public class Vec<E> {

    // This class is a result out of the frustration cause by the inexistence of generic
    // arrays in Java due to type erasure. So this is the very implementation of such a
    // generic array-like structure. It is specifically designed to perform equally as space
    // efficient as a normal array with the small overhead of this class wrapping around the
    // array. The array is always kept in the required size and never to bigger than this,
    // this also gives us the advantage that we do not need to have to remember the current
    // size of our list. The trade off is that modifying the list is expensive, which is
    // acceptable as we only modify the list during parsing and from there on we only
    // iterate over the content, which is fast again.

    @SafeVarargs
    public static <E> Vec<E> of(final E... elements) {
        return new Vec<>(elements);
    }

    private Object[] elements;
    private int size;

    public Vec(final int capacity) {
        elements = new Object[capacity];
    }

    public Vec(final Object[] elements) {
        this.elements = new Object[elements.length];
        System.arraycopy(elements, 0, this.elements, 0, elements.length);

        this.size = elements.length;
    }

    public void add(final E value) {
        if (size == elements.length) {
            final Object[] newElements = new Object[elements.length + 1];

            System.arraycopy(elements, 0, newElements, 0, elements.length);
            this.elements = newElements;
        }

        elements[size++] = value;
    }

    public int length() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public E get(final int i) {
        return (E) elements[i];
    }

    public E getLast() {
        return get(length() - 1);
    }

    public List<E> toList() {
        final List<E> list = new ArrayList<>(elements.length);

        for (int i = 0, length = elements.length; i < length; i++) {
            list.add(get(i));
        }

        return list;
    }
}
