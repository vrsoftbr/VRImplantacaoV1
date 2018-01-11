/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.utils.collection;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class IDStackTest {
    
    public IDStackTest() {
    }
    
    private IDStack stack;
    @Before
    public void setUp() {
        stack = new IDStack(
                    1L,
                    50L,
                    48L,
                    784L,
                    10L,
                    5L,
                    1L,
                    986L,
                    12L,
                    31L,
                    84L,
                    18L,
                    43L,
                    201L,
                    99L,
                    416L,
                    53L,
                    86L,
                    23L,
                    78L,
                    61L
        );        
    }
    
    @After
    public void tearDown() {
        stack.clear();
    }

    @Test
    public void testPop() {        
        getDesc("pop");
        assertEquals(1L, stack.pop());
        assertEquals(5L, stack.pop());
        assertEquals(10L, stack.pop());
        assertEquals(12L, stack.pop());
        assertEquals(18L, stack.pop());
        assertEquals(23L, stack.pop());
        assertEquals(31L, stack.pop());
        assertEquals(43L, stack.pop());
        assertEquals(48L, stack.pop());
        assertEquals(50L, stack.pop());
        assertEquals(53L, stack.pop());
        assertEquals(61L, stack.pop());
        assertEquals(78L, stack.pop());
        assertEquals(84L, stack.pop());
        assertEquals(86L, stack.pop());
        assertEquals(99L, stack.pop());
        assertEquals(201L, stack.pop());
        assertEquals(416L, stack.pop());
        assertEquals(784L, stack.pop());
        assertEquals(986L, stack.pop());
    }

    @Test
    public void testPop2() {
        getDesc("pop");
        IDStack idStack = new IDStack();
        SortedSet<Long> idsSorted = new TreeSet<>();
        
        assertEquals(0, idStack.size());
        
        Random random = new Random();
        for (int i = 1; i <= 10000; i++) {
            long id = random.nextInt(999999);
            idsSorted.add(id);
            idStack.add(id);
        }
        
        int cont = 0;
        for (Long idSorted: idsSorted) {
            cont++;
            Long id = idStack.pop();
            assertEquals(idSorted, id);
        }
        
        assertEquals(idsSorted.size(), cont);
    }
    
    @Test
    public void testClear() {
        getDesc("clear");
        assertEquals(20, this.stack.size());
        this.stack.clear();
        assertEquals(0, this.stack.size());
    }
    
    @Test
    public void testRemover() {
        getDesc("remover");
        assertEquals(20, this.stack.size());
        this.stack.remove(43L);
        this.stack.remove(201L);
        this.stack.remove(5L);
        this.stack.remove(99L);
        this.stack.remove(250L);
        assertEquals(16, this.stack.size());
    }
    
    @Test
    public void testIsEmpty() {
        getDesc("isEmpty");
        assertFalse(this.stack.isEmpty());
        this.stack.clear();
        assertTrue(this.stack.isEmpty());
        this.stack.add(1);
        this.stack.add(10);
        assertFalse(this.stack.isEmpty());
        this.stack.remove(1);
        assertFalse(this.stack.isEmpty());
        this.stack.remove(10);
        assertTrue(this.stack.isEmpty());
    }

    private void getDesc(String desc) {
        System.out.println("IDStack." + desc + "();");
    }
    
    
}
