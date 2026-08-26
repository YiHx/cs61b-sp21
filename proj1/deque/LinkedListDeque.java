package deque;


import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {


    private static class DLList<T> {
        T item;
        DLList<T> prev;
        DLList<T> next;


        DLList(T item, DLList<T> prev, DLList<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;

        }
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int point;

        public LinkedListDequeIterator() {
            point = 0;
        }

        public boolean hasNext() {
            return point < size;
        }

        public T next() {
            T returnValue = get(point);
            point++;
            return returnValue;
        }
    }

    private DLList<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new DLList<T>(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public void addFirst(T item) {
        DLList<T> nextNode = new DLList<T>(item, sentinel, sentinel.next);
        sentinel.next.prev = nextNode;
        sentinel.next = nextNode;
        size++;

    }

    public void addLast(T item) {
        DLList<T> nextNode = new DLList<T>(item, sentinel.prev, sentinel);
        sentinel.prev.next = nextNode;
        sentinel.prev = nextNode;
        size++;
    }

//    public boolean isEmpty() {
//        return size == 0;
//    }

    public int size() {
        return size;
    }

    public void printDeque() {
        DLList<T> curr = sentinel.next;
        while (curr != sentinel) {
            System.out.print(curr.item);
            curr = curr.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size--;
        T item = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        return item;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size--;
        T item = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        return item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) return null;
        DLList<T> curr = sentinel.next;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.item;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<T> other = (Deque<T>) o;
        if (this.size() != other.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    private T innerRecursive(int idx, DLList<T> dll) {
        if (idx == 0) {
            return dll.item;
        }
        return innerRecursive(idx - 1, dll.next);
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        DLList<T> curr = sentinel.next;
        return innerRecursive(index, curr);


    }


}
