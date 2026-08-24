package flik;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestFlik {

    @Test
    public void test1() {
        assertTrue(Flik.isSameNumber(1, 1));
    }

    @Test
    public void test2() {
        assertFalse(Flik.isSameNumber(1, 2));
    }

    @Test
    public void test3() {
        assertTrue(Flik.isSameNumber(128, 128));
    }

}
