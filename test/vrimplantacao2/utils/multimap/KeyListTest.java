/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.utils.multimap;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class KeyListTest {
    
    public KeyListTest() {
    }

    @Test
    public void testCompareTo() {
        KeyList key1 = new KeyList("BASE", "2", 1);
        KeyList key2 = new KeyList("BASE", "2", 1);
        KeyList key3 = new KeyList("BASE", "2", 1, "3");
        KeyList key4 = new KeyList("BASE", "2");
        KeyList key5 = new KeyList("BASE", 5);
        KeyList key6 = new KeyList("BASE", "1");
        
        assertEquals(0, key1.compareTo(key2));
        assertEquals(-1, key1.compareTo(key3));
        assertEquals(-1, key2.compareTo(key3));
        assertEquals(-1, key4.compareTo(key5));
        assertEquals(1, key4.compareTo(key6));
    }
    
    @Test
    public void testEquals() {
        KeyList key1 = new KeyList("BASE", "2", 1);
        KeyList key2 = new KeyList("BASE", "2", 1);
        KeyList key3 = new KeyList("BASE", "2", 1, "3");
        KeyList key4 = new KeyList("BASE", "2");
        KeyList key5 = new KeyList("BASE", 5);
        KeyList key6 = new KeyList("BASE", "1");
        
        assertTrue(key1.equals(key2));
        assertFalse(key1.equals(key3));
        assertFalse(key2.equals(key3));
        assertFalse(key4.equals(key5));
        assertFalse(key4.equals(key6));
    }
    
}
