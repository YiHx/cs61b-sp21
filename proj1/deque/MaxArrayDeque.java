package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    //    public class Comparator<T>{
//        public int compare(T a,T b){
//            retutrn a-b;
//        }
//    }
    private Comparator<T> innerComparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        innerComparator = c;
    }

    public T max() {
        if (innerComparator == null) return null;
        return max(innerComparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) return null;
        T maxItem = get(0);
        for (int i = 0; i < size(); i++) {
            T currItem = get(i);
            if (c.compare(currItem, maxItem) > 0) {
                maxItem = get(i);
            }
        }
        return maxItem;
    }
}
