package vrimplantacao2.utils.multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author LeandroCaires
 */
public class MultiMapTest {
    
    private static class TestClass {
        public String descricao;

        public TestClass(String descricao) {
            this.descricao = descricao;
        }
        
        
    }
    
    public MultiMapTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testPut() {
       MultiMap<String, TestClass> map = new MultiMap<>();
        
       TestClass teste1 = new TestClass("teste1");
       TestClass teste2 = new TestClass("teste2");
       TestClass teste3 = new TestClass("teste3");
       TestClass teste4 = new TestClass("teste4");
       TestClass teste5 = new TestClass("teste5");
       TestClass teste6 = new TestClass("teste6");
       
       //Chaves são criados os objectos são incluídos no map
       map.put(teste1, "NIVEL1", "1");
       map.put(teste2, "NIVEL1", "2");
       map.put(teste3, "NIVEL2", "NIVEL22", "TESTE");
       map.put(teste4, "NIVEL2", "NIVEL22", "TESTE");
       map.put(teste5, "NIVEL3");
       map.put(teste6, "NIVEL3", "1", "TESTE");
       
        assertEquals(teste1, map.get("NIVEL1", "1"));
        assertEquals(teste2, map.get("NIVEL1", "2"));
        assertEquals(teste4, map.get("NIVEL2", "NIVEL22", "TESTE"));
        assertEquals(teste5, map.get("NIVEL3"));
        assertEquals(teste6, map.get("NIVEL3", "1", "TESTE"));
        //Se uma chave não existir retorna null
        assertNull(map.get("VAZIO", "1", "3"));
        assertNull(map.get("NIVEL"));
        assertNull(map.get("VAZIO", "1", "5556"));
    }
    
    @Test
    public void testPutLimiteInteger() {
       MultiMap<Integer, TestClass> map = new MultiMap<>(3);
        
       TestClass teste1 = new TestClass("teste1");
       TestClass teste2 = new TestClass("teste2");
       TestClass teste3 = new TestClass("teste3");
       TestClass teste4 = new TestClass("teste4");
       TestClass teste5 = new TestClass("teste5");
       TestClass teste6 = new TestClass("teste6");
       
       //Chaves são criados os objectos são incluídos no map
       map.put(teste1, 1, 1, 1);
       map.put(teste2, 1, 1, 2);
       map.put(teste3, 2, 1, 1);
       map.put(teste4, 2, 1, 1);
       map.put(teste5, 3, 1, 1);
       map.put(teste6, 3, 2, 1);
       
        assertEquals(teste1, map.get(1, 1, 1));
        assertEquals(teste2, map.get(1, 1, 2));
        assertEquals(teste4, map.get(2, 1, 1));
        assertNull(map.get(3));
        assertEquals(teste5, map.get(3, 1, 1));
        assertEquals(teste6, map.get(3, 2, 1));
        //Se uma chave não existir retorna null
        assertNull(map.get(0, 1));
        assertNull(map.get(1, 1, 3));
        assertNull(map.get(0, 1, 1));
    }
    
    @Test(expected = MultiMapException.class)
    public void testLimiteErrado() {
        MultiMap<Integer, TestClass> map = new MultiMap<>(3);
        
        TestClass teste1 = new TestClass("teste1");
        
        map.put(teste1, 1, 1, 1, 1);
    }
    
    @Test
    public void testSize() {
        MultiMap<Integer, Integer> map = new MultiMap<>(3);

        map.put(1, 1, 1, 1);
        map.put(2, 1, 1, 2);
        map.put(3, 2, 1, 1);
        map.put(4, 2, 1, 1);
        map.put(5, 3, 1, 1);
        map.put(6, 3, 2, 1);
       
        assertEquals(5, map.size());
    }
    
    @Test
    public void testIsEmpty() {
        MultiMap<Integer, Integer> map = new MultiMap<>();
        
        assertTrue(map.isEmpty());

        map.put(1, 1, 1, 1);
        
        assertFalse(map.isEmpty());        
    }
    
    @Test
    public void testRemove() {
        MultiMap<Integer, Integer> map = new MultiMap<>();
        
        assertEquals(0, map.size());

        map.put(1, 1, 1, 1);
        map.put(2, 1, 1, 2);
        map.put(3, 2, 1);
        map.put(4, 3, 1, 1);
        
        assertEquals(4, map.size());
        
        assertEquals((Integer) 2, map.remove(1,1,2));
        assertEquals(3, map.size());
        
        assertEquals((Integer) 3, map.remove(2, 1));
        assertEquals(2, map.size());
        
        assertNull(map.remove(4));
        assertEquals(2, map.size());
    }
    
    @Test
    public void testClear() {
        MultiMap<Integer, Integer> map = new MultiMap<>();
        
        assertEquals(0, map.size());

        map.put(1, 1, 1, 1);
        map.put(2, 1, 1, 2);
        map.put(3, 2, 1);
        map.put(4, 3, 1, 1);
        
        assertEquals(4, map.size());
        
        map.clear();
        
        assertEquals(0, map.size());
    }
    
    @Test
    public void testKeySet() {
        MultiMap<Integer, Integer> map = new MultiMap<>();
        
        assertEquals(0, map.size());
        assertEquals(0, map.keySet().size());

        map.put(1, 1, 1, 1);
        map.put(2, 1, 1, 2);
        map.put(3, 2, 1);
        map.put(4, 3, 1, 1);
        map.put(10, 3, 1, 1, 9);
        map.put(11, 3, 1, 1, 9, 20, 50);
        map.put(19, 3, 1, 1, 9, 20, 6);
        
        Set<KeyList<Integer>> keySet = map.keySet();   
        assertEquals(7, map.keySet().size());
        
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {1,1,1})));
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {1,1,2})));
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {2,1})));
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {3,1,1})));
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {3,1,1,9})));
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {3,1,1,9,20,50})));
        assertTrue(keySet.contains(new KeyList<>(new Integer[] {3,1,1,9,20,6})));
    }
    
    @Test
    public void testValues() {
        MultiMap<Integer, Integer> map = new MultiMap<>();
        
        assertEquals(0, map.size());
        assertEquals(0, map.keySet().size());

        map.put(1, 1, 1, 1);
        map.put(2, 1, 1, 2);
        map.put(3, 2, 1);
        map.put(4, 3, 1, 1);
        map.put(10, 3, 1, 1, 9);
        map.put(11, 3, 1, 1, 9, 20, 50);
        map.put(19, 3, 1, 1, 9, 20, 6);
        
        List<Integer> values = new ArrayList<>(map.values());   
        assertEquals(7, map.keySet().size());
        
        assertEquals((Integer) 1, values.get(0));
        assertEquals((Integer) 2, values.get(1));
        assertEquals((Integer) 3, values.get(2));
        assertEquals((Integer) 4, values.get(3));
        assertEquals((Integer) 10, values.get(4));
        assertEquals((Integer) 11, values.get(5));
        assertEquals((Integer) 19, values.get(6));
    }
    
    @Test
    public void testGetSortedList() {
        MultiMap<Integer, Integer> map = new MultiMap<>();

        map.put(11, 3, 1, 1, 9, 20, 50);
        map.put(4, 3, 1, 1);
        map.put(1, 1, 1, 1);
        map.put(19, 3, 1, 1, 9, 20, 6);
        map.put(2, 1, 1, 2);
        map.put(3, 2, 1);
        map.put(10, 3, 1, 1, 9);
        
        MultiMap<Integer, Integer> sortedMap = map.getSortedMap();
        ArrayList<KeyList<Integer>> list = new ArrayList<>(sortedMap.keySet());
            
        assertArrayEquals(new Integer[] {1,1,1}, list.get(0).toArray());
        assertArrayEquals(new Integer[] {1,1,2}, list.get(1).toArray());
        assertArrayEquals(new Integer[] {2,1}, list.get(2).toArray());
        assertArrayEquals(new Integer[] {3,1,1}, list.get(3).toArray());
        assertArrayEquals(new Integer[] {3,1,1,9}, list.get(4).toArray());
        assertArrayEquals(new Integer[] {3,1,1,9,20,6}, list.get(5).toArray());
        assertArrayEquals(new Integer[] {3,1,1,9,20,50}, list.get(6).toArray());        
        
    }
    
    @Test
    public void testGetSortedList2() {
        MultiMap<Comparable, String> map = new MultiMap<>();

        map.put("TESTE1");
        map.put("TESTE2","ASD");
        map.put("TESTE3","ASD",12,"BA");
        map.put("TESTE4","ASD","BA","BA");
        map.put("TESTE5","ASD",12,13);
        map.put("TESTE6","ASD","BA");
        map.put("TESTE7","ASD","12  ",14);
        map.put("TESTE8","ASD","12   ",20);
        map.put("TESTE9","ASD","111",40);
        map.put("TESTE10","ASD",105,60);
        map.put("TESTE11",111,"AA",60);
        map.put("TESTE5","ASD",10.56,13);
        map.put("TESTE6","Base","11.887.254.0001-90","3,","3,");
        map.put("TESTE7","Base","11.887.254.0001-90","3,","3123");
        
        MultiMap<Comparable, String> sortedMap = map.getSortedMap();
        ArrayList<KeyList<Comparable>> list = new ArrayList<>(sortedMap.keySet());

            
        assertArrayEquals(new Comparable[] {}, list.get(0).toArray());
        assertArrayEquals(new Comparable[] {111,"AA",60}, list.get(1).toArray());
        assertArrayEquals(new Comparable[] {"ASD"}, list.get(2).toArray());
        assertArrayEquals(new Comparable[] {"ASD",10.56,13}, list.get(3).toArray());
        assertArrayEquals(new Comparable[] {"ASD",12,13}, list.get(4).toArray());
        assertArrayEquals(new Comparable[] {"ASD",12,"BA"}, list.get(5).toArray());
        assertArrayEquals(new Comparable[] {"ASD",105,60}, list.get(6).toArray());
        assertArrayEquals(new Comparable[] {"ASD","111",40}, list.get(7).toArray());
        assertArrayEquals(new Comparable[] {"ASD","12  ",14}, list.get(8).toArray());
        assertArrayEquals(new Comparable[] {"ASD","12   ",20}, list.get(9).toArray());
        assertArrayEquals(new Comparable[] {"ASD","BA"}, list.get(10).toArray());
        assertArrayEquals(new Comparable[] {"ASD","BA", "BA"}, list.get(11).toArray());
        assertArrayEquals(new Comparable[] {"Base","11.887.254.0001-90","3,","3,"}, list.get(12).toArray());
        assertArrayEquals(new Comparable[] {"Base","11.887.254.0001-90","3,","3123"}, list.get(13).toArray());
        
        
    }
}
