package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizadorTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProdutoRepository repository;
    
    @Before
    public void init(){
        when(repository.getLoja()).thenReturn("LOJA 02");
        when(repository.getSistema()).thenReturn("TESTE");
    }
    
    public OrganizadorTest() {
    }

    @Test
    public void testOrganizarIds() throws Exception {
    }

    @Test
    public void testOrganizarListagem() throws Exception {
    }

    @Test
    public void testEliminarDuplicados() throws Exception {
        System.out.print("OrganizadorTest.testEliminarDuplicados...");
        
        List<ProdutoIMP> imports = new ArrayList<>();
        ProdutoIMP imp1 = ProdutoRepositoryTest.getProdutoIMP_MOCA();
        ProdutoIMP imp2 = ProdutoRepositoryTest.getProdutoIMP_MOCA2();
        ProdutoIMP imp3 = ProdutoRepositoryTest.getProdutoIMP_ACEM();
        imports.add(imp1);
        imports.add(imp2);
        imports.add(imp3);
        
        List<ProdutoIMP> results;        
        results = new Organizador(repository).eliminarDuplicados(imports);        
        assertEquals(2, results.size());
        
        imp2.setEan("00007891000100103");
        results = new Organizador(repository).eliminarDuplicados(imports);        
        assertEquals(3, results.size());
        
        imp2.setEan("17891000100103");
        results = new Organizador(repository).eliminarDuplicados(imports);        
        assertEquals(3, results.size());
        
        System.out.println("OK");
    }

    @Test
    public void testSepararBalancaNormaisManterEAN() throws Exception {
    }
    
}
