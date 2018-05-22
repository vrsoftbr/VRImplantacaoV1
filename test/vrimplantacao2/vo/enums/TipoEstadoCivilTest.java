package vrimplantacao2.vo.enums;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class TipoEstadoCivilTest {
   
    @Test
    public void testGetByString() {
        System.out.print("TipoEstadoCivilTest.testGetByString()...");
        
        assertEquals(TipoEstadoCivil.SOLTEIRO, TipoEstadoCivil.getByString(""));
        
        System.out.println("OK");
    }
    
}
