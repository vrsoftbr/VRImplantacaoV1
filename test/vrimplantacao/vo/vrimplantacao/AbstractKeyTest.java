package vrimplantacao.vo.vrimplantacao;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class AbstractKeyTest {
    
    public AbstractKeyTest() {
    }

    /**
     * Test of getItemId method, of class AbstractKey.
     */
    @Test
    public void testGetItemId() {
        System.out.println("getItemId");
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "123456");
        String expResult = "123456";
        String result = instance.getItemId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLojaId method, of class AbstractKey.
     */
    @Test
    public void testSetLojaId() {
        System.out.println("setLojaId");
        String lojaId = "2";
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "12456");
        instance.setLojaId(lojaId);
        assertEquals("2", instance.getLojaId());
        instance.setLojaId(null);
        assertEquals("", instance.getLojaId());
    }

    /**
     * Test of setSistemaId method, of class AbstractKey.
     */
    @Test
    public void testSetSistemaId() {
        System.out.println("setSistemaId");
        String sistemaId = "TESTE SIS";
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "12456");
        instance.setSistemaId(sistemaId);
        assertEquals("TESTE SIS", instance.getSistemaId());
        instance.setSistemaId(null);
        assertEquals("", instance.getSistemaId());
    }

    /**
     * Test of getLojaId method, of class AbstractKey.
     */
    @Test
    public void testGetLojaId() {
        System.out.println("getLojaId");
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "12456");
        String expResult = "1";
        String result = instance.getLojaId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSistemaId method, of class AbstractKey.
     */
    @Test
    public void testGetSistemaId() {
        System.out.println("getSistemaId");
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "12456");
        String expResult = "TESTE";
        String result = instance.getSistemaId();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class AbstractKey.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "123456");
        String expResult = "TESTE-1-123456";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class AbstractKey.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = new AbstractKeyImpl("TESTE", "1", "12456");
        AbstractKey instance = new AbstractKeyImpl("TESTE", "1", "12456");
        assertTrue(instance.equals(obj));
        obj = null;
        assertFalse(instance.equals(obj));
        assertFalse(instance.equals("TESTE"));
        obj = new AbstractKeyImpl("TESTE222", "1", "456-7888");
        assertFalse(instance.equals(obj));
    }

    public class AbstractKeyImpl extends AbstractKey {
        
        private String itemID;

        public AbstractKeyImpl(String sistemaId, String lojaId, String itemID) {
            this.itemID = itemID;
            setSistemaId(sistemaId);
            setLojaId(lojaId);
        }

        public String getItemId() {
            return itemID;
        }
    }
    
}
