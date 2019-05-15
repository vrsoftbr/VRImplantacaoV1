package vrimplantacao.vo.vrimplantacao;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class NutricionalToledoVOTest {
    
    public NutricionalToledoVOTest() {
    }

    @Test
    public void testAddMensagemAlergico() {
        System.out.print("NutricionalToledoVOTest.testAddMensagemAlergico()...");
        
        NutricionalToledoVO n = new NutricionalToledoVO();
        
        n.addMensagemAlergico("AAAAAAAAA AAAAAAAAA AAAAAAAAA AAAAAAAAA AAAAAAAAA AAAAAABBB BBBBBBBBB BBBBBBBBB BBBBBBBBB BBBBBBBBB BBBBBBBBB BB CCCCCCCCCC");
        
        assertEquals(3, n.getMensagemAlergico().size());
        assertEquals("AAAAAAAAA AAAAAAAAA AAAAAAAAA AAAAAAAAA AAAAAAAAA AAAAAA", n.getMensagemAlergico().get(0));
        assertEquals("BBB BBBBBBBBB BBBBBBBBB BBBBBBBBB BBBBBBBBB BBBBBBBBB BB", n.getMensagemAlergico().get(1));
        assertEquals(" CCCCCCCCCC", n.getMensagemAlergico().get(2));
        
        System.out.println("OK");
    }

    
}
