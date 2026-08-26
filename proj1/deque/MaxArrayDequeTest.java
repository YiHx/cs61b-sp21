package deque;


import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void test1() {
        Comparator<Integer> curr = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };

        MaxArrayDeque<Integer> test1 = new MaxArrayDeque<>(curr);

        test1.addFirst(1);
        test1.addLast(2);
        test1.addFirst(17);
        test1.addFirst(17);
        test1.addLast(70);
        assertEquals(70, (int) test1.max());
        test1.removeLast();
        assertEquals(17, (int) test1.max());

    }
}
