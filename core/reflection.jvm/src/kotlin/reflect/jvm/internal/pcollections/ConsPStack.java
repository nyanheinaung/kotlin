/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.pcollections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple persistent stack of non-null values.
 * <p/>
 * This implementation is thread-safe, although its iterators may not be.
 */
final class ConsPStack<E> implements Iterable<E> {
    private static final ConsPStack<Object> EMPTY = new ConsPStack<Object>();

    @SuppressWarnings("unchecked")
    public static <E> ConsPStack<E> empty() {
        return (ConsPStack<E>) EMPTY;
    }

    final E first;
    final ConsPStack<E> rest;
    private final int size;

    private ConsPStack() { // EMPTY constructor
        size = 0;
        first = null;
        rest = null;
    }

    private ConsPStack(E first, ConsPStack<E> rest) {
        this.first = first;
        this.rest = rest;
        this.size = 1 + rest.size;
    }

    public E get(int index) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();

        try {
            return iterator(index).next();
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    @Override
    public Iterator<E> iterator() {
        return iterator(0);
    }

    public int size() {
        return size;
    }

    private Iterator<E> iterator(int index) {
        return new Itr<E>(subList(index));
    }

    private static class Itr<E> implements Iterator<E> {
        private ConsPStack<E> next;

        public Itr(ConsPStack<E> first) {
            this.next = first;
        }

        @Override
        public boolean hasNext() {
            return next.size > 0;
        }

        @Override
        public E next() {
            E e = next.first;
            next = next.rest;
            return e;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public ConsPStack<E> plus(E e) {
        return new ConsPStack<E>(e, this);
    }

    private ConsPStack<E> minus(Object e) {
        if (size == 0) return this;
        if (first.equals(e)) // found it
            return rest; // don't recurse (only remove one)
        // otherwise keep looking:
        ConsPStack<E> newRest = rest.minus(e);
        if (newRest == rest) return this;
        return new ConsPStack<E>(first, newRest);
    }

    public ConsPStack<E> minus(int i) {
        return minus(get(i));
    }

    private ConsPStack<E> subList(int start) {
        if (start < 0 || start > size)
            throw new IndexOutOfBoundsException();
        if (start == 0)
            return this;
        return rest.subList(start - 1);
    }
}
