package deque;

import static org.junit.Assert.*;


import org.junit.Test;


public class ArrayDequeTest {

    @Test
    public void test1(){
        ArrayDeque<Integer> test1 =new ArrayDeque<>();
        test1.addFirst(1);
        test1.addFirst(2);
        test1.addFirst(3);
        test1.addLast(4);
        assertEquals(4, test1.size());
        test1.removeFirst();
        assertEquals(3, test1.size());
        test1.removeLast();
        assertEquals(2, test1.size());
        test1.removeLast();
        test1.removeFirst();
        assertTrue(test1.isEmpty());
    }

    @Test
    public void test2(){
        ArrayDeque<String> test2 =new ArrayDeque<>();
        test2.addFirst("Big");
        test2.addFirst("Dick");
        test2.addFirst("Fuck");
        test2.addLast("Bitch");
        assertEquals(4, test2.size());
        test2.removeFirst();
        assertEquals(3, test2.size());
        test2.removeLast();
        assertEquals(2, test2.size());
        test2.removeLast();
        test2.removeFirst();
        assertTrue(test2.isEmpty());
    }

}


