package vrimplantacao2.dao.cadastro.produto2;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;
import vrimplantacao2.utils.collection.IDStack;


@RunWith(MockitoJUnitRunner.class)
public class ProdutoIDStackTest {
    
    @Mock private ProdutoIDStackProvider provider;
    private ProdutoIDStack produtoIDStack;
    
    @Before
    public void init() {
        try {
            TreeSet<Integer> set = new TreeSet<>(
                    Arrays.asList(
                            new Integer[] {1,5,69,458,12,65,758,13111,69,73,7,10100,48427}
                    )
            );
            when(provider.getIDsCadastrados()).thenReturn(set);
            
            IDStack balanca = new IDStack();
            for (int i = 9999; i >= 1; i--) {
                if (!set.contains(i)) {
                    balanca.add(i);
                }
            }            
            when(provider.getIDsVagosBalanca()).thenReturn(balanca);
            
            IDStack normais = new IDStack();
            for (int i = 999999; i >= 10000; i--) {
                if (!set.contains(i)) {
                    normais.add(i);
                }
            }
            when(provider.getIDsVagosNormais()).thenReturn(normais);
            
            produtoIDStack = new ProdutoIDStack(provider);
            
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testTesteMock() throws Exception {
        System.out.println("testeMock");
        Set<Integer> cadastrados = provider.getIDsCadastrados();
        IDStack balanca = provider.getIDsVagosBalanca();
        while (!balanca.isEmpty()) {
            assertTrue(!cadastrados.contains((int) balanca.pop()));
        }
        IDStack normais = provider.getIDsVagosNormais();
        while (!normais.isEmpty()) {
            assertTrue(!cadastrados.contains((int) normais.pop()));
        }        
    }

    private void print(String desc) {
        System.out.println("ProdutoIDStack." + desc + "()");
    }
    
    @Test
    public void testObterID() throws Exception {
        print("obterID");
        
        //1,5,69,458,12,65,758,13111,69,73,7,10100,48427
        
        assertEquals(2 ,produtoIDStack.obterID("6 ", true));
        assertEquals(6 ,produtoIDStack.obterID("6", true));
        assertEquals(3 ,produtoIDStack.obterID("6ASDsd", true));
        assertEquals(4 ,produtoIDStack.obterID("458", true));
        assertEquals(9 ,produtoIDStack.obterID("9", false)); 
        assertEquals(8 ,produtoIDStack.obterID("7891000100103", true));
        assertEquals(10000 ,produtoIDStack.obterID("10100", false));
        assertEquals(10001 ,produtoIDStack.obterID("544483ASDsd", false));
        assertEquals(544483 ,produtoIDStack.obterID("544483", false));
        assertEquals(10002 ,produtoIDStack.obterID("7891000100103", false));
        
    } 
}
