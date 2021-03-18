package vrimplantacao2.parametro;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author leandro
 */
public class VersaoTest {
    
    public VersaoTest() {
    }

    @Test
    public void testIgual() {
        
        Versao.carregar(3,21,10);
        
        assertFalse(Versao.igual(2));
        assertFalse(Versao.igual(2,21));
        assertFalse(Versao.igual(2,21,11));
        assertTrue (Versao.igual(3));
        assertFalse(Versao.igual(3,19));
        assertFalse(Versao.igual(3,19,1));
        assertTrue (Versao.igual(3,21));
        assertFalse(Versao.igual(3,21,0));
        assertFalse(Versao.igual(3,21,0,23));
        assertFalse(Versao.igual(3,21,9));
        assertTrue (Versao.igual(3,21,10));
        assertTrue (Versao.igual(3,21,10,108));
        assertTrue (Versao.igual(3,21,10,108,20));
        assertFalse(Versao.igual(3,21,11));
        assertFalse(Versao.igual(3,21,11,1));
        assertFalse(Versao.igual(3,21,12));
        assertFalse(Versao.igual(3,22));
        assertFalse(Versao.igual(4));
        assertFalse(Versao.igual(4,0,23));
        assertFalse(Versao.igual(4,5));
        assertFalse(Versao.igual(4,5,10));
        
    }
    
    @Test
    public void testIgualOuMaiorQue() {
        
        Versao.carregar(3,21,10);
        
        assertTrue (Versao.igualOuMaiorQue(2));
        assertTrue (Versao.igualOuMaiorQue(2,21));
        assertTrue (Versao.igualOuMaiorQue(2,21,11));
        assertTrue (Versao.igualOuMaiorQue(3));
        assertTrue (Versao.igualOuMaiorQue(3,19));
        assertTrue (Versao.igualOuMaiorQue(3,19,1));
        assertTrue (Versao.igualOuMaiorQue(3,21));
        assertTrue (Versao.igualOuMaiorQue(3,21,0));
        assertTrue (Versao.igualOuMaiorQue(3,21,0,23));
        assertTrue (Versao.igualOuMaiorQue(3,21,9));
        assertTrue (Versao.igualOuMaiorQue(3,21,10));
        assertTrue (Versao.igualOuMaiorQue(3,21,10,108));
        assertTrue (Versao.igualOuMaiorQue(3,21,10,108,20));
        assertFalse(Versao.igualOuMaiorQue(3,21,11));
        assertFalse(Versao.igualOuMaiorQue(3,21,11,1));
        assertFalse(Versao.igualOuMaiorQue(3,21,12));
        assertFalse(Versao.igualOuMaiorQue(3,22));
        assertFalse(Versao.igualOuMaiorQue(4));
        assertFalse(Versao.igualOuMaiorQue(4,0,23));
        assertFalse(Versao.igualOuMaiorQue(4,5));
        assertFalse(Versao.igualOuMaiorQue(4,5,10));
        
    }
    
    @Test
    public void testIgualOuMenorQue() {
        
        Versao.carregar(3,21,10);
        
        assertFalse(Versao.igualOuMenorQue(2));
        assertFalse(Versao.igualOuMenorQue(2,21));
        assertFalse(Versao.igualOuMenorQue(2,21,11));
        assertTrue (Versao.igualOuMenorQue(3));
        assertFalse(Versao.igualOuMenorQue(3,19));
        assertFalse(Versao.igualOuMenorQue(3,19,1));
        assertTrue (Versao.igualOuMenorQue(3,21));
        assertFalse (Versao.igualOuMenorQue(3,21,0));
        assertFalse (Versao.igualOuMenorQue(3,21,0,23));
        assertFalse (Versao.igualOuMenorQue(3,21,9));
        assertTrue (Versao.igualOuMenorQue(3,21,10));
        assertTrue (Versao.igualOuMenorQue(3,21,10,108));
        assertTrue (Versao.igualOuMenorQue(3,21,10,108,20));
        assertTrue (Versao.igualOuMenorQue(3,21,11));
        assertTrue (Versao.igualOuMenorQue(3,21,11,1));
        assertTrue (Versao.igualOuMenorQue(3,21,12));
        assertTrue (Versao.igualOuMenorQue(3,22));
        assertTrue (Versao.igualOuMenorQue(4));
        assertTrue (Versao.igualOuMenorQue(4,0,23));
        assertTrue (Versao.igualOuMenorQue(4,5));
        assertTrue (Versao.igualOuMenorQue(4,5,10));
        
    }
    
}
