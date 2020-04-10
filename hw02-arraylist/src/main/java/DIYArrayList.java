import java.util.*;

public class DIYArrayList<E> implements List<E> {
    private static final int INIT_CAPACITY = 20;
    private static final int GROW_STEP = 10;

    private Object[] content;
    private int actualSize = 0;

    public DIYArrayList() {
        this(INIT_CAPACITY);
    }

    public DIYArrayList(int capacity) {
        content = new Object[capacity];
    }

    public DIYArrayList(Collection<E> copy) {
        this(copy.size());
        addAll(copy);
    }

    @Override
    public boolean add(E e) {
        adjustArraySize();
        content[actualSize++] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public E get(int i) {
        Objects.checkIndex(i, actualSize);
        return (E) content[i];
    }

    @Override
    public E set(int i, E e) {
        Objects.checkIndex(i, actualSize);
        E oldVal = (E) content[i];
        content[i] = e;
        return oldVal;
    }

    @Override
    public void add(int i, E e) {
        Objects.checkIndex(i, actualSize);
        adjustArraySize();
        E oldVal = set(i, e);
        actualSize++;
        for (int shiftIndex = i + 1; shiftIndex < actualSize; shiftIndex++) {
            oldVal = set(shiftIndex, oldVal);
        }
    }

    @Override
    public E remove(int i) {
        Objects.checkIndex(i, actualSize);
        E value = (E) content[i];
        if (actualSize > 1) {
            for (int shiftIndex = i; shiftIndex < actualSize - 1; shiftIndex++) {
                content[shiftIndex] = content[shiftIndex + 1];
            }
            content[actualSize - 1] = null;
        }
        actualSize--;
        adjustArraySize();
        return value;
    }

    private void adjustArraySize() {
        int newSize = -1;

        if (content.length == actualSize) {
            newSize = actualSize + GROW_STEP;
        } else if (content.length / 2 > actualSize) {
            newSize = content.length / 2;
        } else if (actualSize == 0 && content.length != 0) {
            newSize = 0;
        }

        if (newSize >= 0) {
            content = newSize == 0 ? new Object[0] : Arrays.copyOf(content, newSize);
        }
    }

    @Override
    public int size() {
        return actualSize;
    }

    @Override
    public boolean isEmpty() {
        return actualSize == 0;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < actualSize; i++) {
            if (Objects.equals(o, content[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        if (actualSize > 0) {
            var newArray = Arrays.copyOf(content, actualSize);
            Arrays.sort(newArray, (Comparator) c);
            content = newArray;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public void clear() {
        content = new Object[0];
        actualSize = 0;
    }

    @Override
    public String toString() {
        return Arrays.toString(content);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        int newSize = collection.size() + actualSize;
        if (newSize > this.content.length) {
            content = Arrays.copyOf(this.content, newSize);
        }
        for (E e : collection) {
            content[actualSize++] = e;
        }
        return true;
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return new ListIterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < actualSize;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (E) content[currentIndex++];
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex != 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                return (E) content[--currentIndex];
            }

            @Override
            public int nextIndex() {
                return Math.min(currentIndex + 1, actualSize);
            }

            @Override
            public int previousIndex() {
                return currentIndex - 1;
            }

            @Override
            public void remove() {
                DIYArrayList.this.remove(--currentIndex);
            }

            @Override
            public void set(E e) {
                DIYArrayList.this.set(currentIndex - 1, e);
            }

            @Override
            public void add(E e) {
                DIYArrayList.this.add(currentIndex - 1, e);
            }
        };
    }

    @Override
    public List<E> subList(int i, int i1) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean addAll(int i, Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

}
