package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> test1 = new AListNoResizing<>();
        BuggyAList<Integer> test2 = new BuggyAList<>();
        test1.addLast(1);
        test1.addLast(2);
        test1.addLast(3);

        test2.addLast(1);
        test2.addLast(2);
        test2.addLast(3);

        assertEquals(test1.removeLast(), test2.removeLast());
        assertEquals(test1.removeLast(), test2.removeLast());
        assertEquals(test1.removeLast(), test2.removeLast());


    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {

            int operationNumber = StdRandom.uniform(0, 4);

            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {

                int sizeCorrect = correct.size();
                int sizeBroken = broken.size();
                System.out.println("size: correct=" + sizeCorrect + ", broken=" + sizeBroken);
                assertEquals(sizeCorrect, sizeBroken);
            } else if (operationNumber == 2) {

                if (correct.size() > 0 && broken.size() > 0) {
                    int lastCorrect = correct.getLast();
                    int lastBroken = broken.getLast();
                    System.out.println("getLast: correct=" + lastCorrect + ", broken=" + lastBroken);
                    assertEquals(lastCorrect, lastBroken);
                }
            } else if (operationNumber == 3) {

                if (correct.size() > 0 && broken.size() > 0) {
                    int removedCorrect = correct.removeLast();
                    int removedBroken = broken.removeLast();
                    System.out.println("removeLast: correct=" + removedCorrect + ", broken=" + removedBroken);
                    assertEquals(removedCorrect, removedBroken);
                }
            }
        }
    }
}
