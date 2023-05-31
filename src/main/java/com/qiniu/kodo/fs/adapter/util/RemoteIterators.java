package com.qiniu.kodo.fs.adapter.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RemoteIterators {
    public static <T> RemoteIterator<T> remoteIteratorFromSingleton(T e) {
        return new RemoteIterator<T>() {
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public T next() {
                if (hasNext) {
                    hasNext = false;
                    return e;
                } else {
                    throw new IllegalStateException("No more elements");
                }
            }
        };
    }

    public static <I,O> RemoteIterator<O> mappingRemoteIterator(RemoteIterator<I> iterator, Function<I,O> mapper) {
        return new RemoteIterator<O>() {
            @Override
            public boolean hasNext() throws IOException {
                return iterator.hasNext();
            }

            @Override
            public O next() throws IOException {
                return mapper.apply(iterator.next());
            }
        };
    }

    public static <T> List<T> toList(RemoteIterator<T> iterator) throws IOException {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    public static <T> T[] toArray(RemoteIterator<T> iterator, T[] array) throws IOException {
        List<T> list = toList(iterator);
        return list.toArray(array);
    }
}
