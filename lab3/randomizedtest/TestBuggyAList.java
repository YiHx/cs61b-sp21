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
    public void testThreeAddThreeRemove(){
      AListNoResizing<Integer> test1 =new AListNoResizing<>();
      BuggyAList<Integer> test2 = new BuggyAList<>();
      test1.addLast(1);
      test1.addLast(2);
      test1.addLast(3);

      test2.addLast(1);
      test2.addLast(2);
      test2.addLast(3);

      assertEquals(test1.removeLast(),test2.removeLast());
      assertEquals(test1.removeLast(),test2.removeLast());
      assertEquals(test1.removeLast(),test2.removeLast());


  }
}
