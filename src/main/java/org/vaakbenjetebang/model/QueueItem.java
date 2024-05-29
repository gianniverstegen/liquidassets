package org.vaakbenjetebang.model;

public class QueueItem<T> {

    private final T item;
    private final boolean isSentinel;

    public static <T> QueueItem<T> of(T item) {
        return new QueueItem<>(item, false);
    }

    public static <T> QueueItem<T> sentinel() {
        return new QueueItem<>(null, true);
    }

    public boolean isSentinel() {
        return isSentinel;
    }

    public T getItem() {
        if (isSentinel) {
            throw new IllegalStateException("QueueItem is sentinel and does not contain an item");
        }

        return item;
    }

    private QueueItem(T item, boolean isSentinel) {
        if (item == null && !isSentinel) {
            throw new IllegalArgumentException("Values cannot both be null");
        }
        if (item != null && isSentinel) {
            throw new IllegalArgumentException("Values cannot both be present");
        }

        this.item = item;
        this.isSentinel = isSentinel;
    }
}
