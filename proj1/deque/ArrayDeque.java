package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] array;
    private int size;
    private int nextLast;
    private int nextFirst;

    public ArrayDeque() {
        array = (T[]) new Object[8];
        size = 0;
        nextLast = 1;
        nextFirst = 0;
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int curr;

        public ArrayDequeIterator() {
            curr = 0;
        }

        public boolean hasNext() {
            return curr < size;
        }

        public T next() {
            T returnValue = get(curr);
            curr++;
            return returnValue;
        }
    }

    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        int curr = (nextFirst + 1) % array.length;
        for (int i = 0; i < size; i++) {
            newArray[i] = array[curr];
            curr = (curr + 1) % array.length;
        }
        array = newArray;
        nextFirst = capacity - 1;
        nextLast = size;
    }


    private void checkCapacity() {
        if (size == array.length) {
            resize(array.length * 2);
        } else if (array.length >= 16 && (double) size / array.length < 0.25) {
            resize(array.length / 2);
        }
    }


    public void addFirst(T item) {
        checkCapacity();
        array[nextFirst] = item;
        nextFirst = (nextFirst - 1 + array.length) % array.length;
        size++;
    }

    public void addLast(T item) {
        checkCapacity();
        array[nextLast] = item;
        nextLast = (nextLast + 1) % array.length;
        size++;
    }

//    public boolean isEmpty() {
//        return size == 0;
//    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int curr = (nextFirst + 1) % array.length;
        for (int i = 0; i < size; i++) {
            System.out.print(array[curr]);
            curr = (curr + 1) % array.length;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T item = array[(nextFirst + 1) % array.length];
        array[(nextFirst + 1) % array.length] = null;
        nextFirst = (nextFirst + 1) % array.length;
        size--;
        checkCapacity();
        return item;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T item = array[(nextLast - 1 + array.length) % array.length];
        array[(nextLast - 1 + array.length) % array.length] = null;
        nextLast = (nextLast - 1 + array.length) % array.length;
        size--;
        checkCapacity();
        return item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[(nextFirst + 1 + index) % array.length];
    }


    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
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


}

