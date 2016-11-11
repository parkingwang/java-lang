package com.parkingwng.lang.data;

import com.parkingwng.lang.kit.ListKit;
import com.parkingwng.lang.kit.ObjectKit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Yoojia Chen (yoojiachen@gmail.com)
 * @since 1.0
 */
public class ImmutableList<E> implements RandomAccess {

    private final E[] mElementData;
    private final int mSize;

    public ImmutableList(E[] data) {
        this.mElementData = ObjectKit.notNull(data);
        this.mSize = mElementData.length;
    }

    public E get(int position) {
        return mElementData[position];
    }

    public int size(){
        return mElementData.length;
    }

    public boolean isEmpty() {
        return 0 == mElementData.length;
    }

    public boolean contains(E o) {
        return indexOf(o) >= 0;
    }

    @NotNull
    public Iterator<E> iterator() {
        return new Itr();
    }

    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = mSize -1; i >= 0; i--)
                if (mElementData[i]==null)
                    return i;
        } else {
            for (int i = mSize -1; i >= 0; i--)
                if (o.equals(mElementData[i]))
                    return i;
        }
        return -1;
    }

    public int indexOf(E o) {
        if (o == null) {
            for (int i = 0; i < mSize; i++)
                if (mElementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < mSize; i++)
                if (o.equals(mElementData[i]))
                    return i;
        }
        return -1;
    }

    public ArrayList<E> toList(){
        return ListKit.arrayListOf(mElementData);
    }

    public Object[] toArray() {
        return Arrays.copyOf(mElementData, mSize);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < mSize) {
            return (T[]) Arrays.copyOf(mElementData, mSize, a.getClass());
        }
        System.arraycopy(mElementData, 0, a, 0, mSize);
        if (a.length > mSize) {
            a[mSize] = null;
        }
        return a;
    }

    @Override
    public Object clone() {
        return new ImmutableList<>(Arrays.copyOf(mElementData, mSize));
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<E> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        public boolean hasNext() {
            return cursor != mSize;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            int i = cursor;
            if (i >= mSize)
                throw new NoSuchElementException();
            Object[] elementData = mElementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}