package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import static org.mockito.Matchers.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

@RunWith(MockitoJUnitRunner.class)
public class UnificadorProdutoRepositoryTest {
    
    public UnificadorProdutoRepositoryTest() {
    }
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProdutoRepositoryProvider provider;

    @Test
    public void testUnificar2() throws Exception {
        TestAux aux = new TestAux();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }).when(provider).salvar(any(ProdutoVO.class));
        
        UnificadorProdutoRepository repository = new UnificadorProdutoRepository(provider);
        
        ArrayList<ProdutoIMP> lista = new ArrayList<>();
        lista.add(aux.impForTest("1", "MOCA", "789"));
        lista.add(aux.impForTest("1", "MOCA", "789"));
        lista.add(aux.impForTest("1", "MOCA", "789"));
        
        repository.unificar(lista);        
        
    }
    
    @Test
    public void filtrarEansValidosParaUnificacao() {        
        TestAux aux = new TestAux();
        List<ProdutoIMP> produtos = new ArrayList<>();
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("2", "ACEM", "345"));
        produtos.add(aux.impForTest("5", "PICANHA", "45"));
        produtos.add(aux.impForTest("52", "CANETA BIC", "12345678"));
        
        UnificadorProdutoRepository rep = new UnificadorProdutoRepository(provider);
        produtos = rep.filtrarEansValidosParaUnificacao(produtos);
        
        assertEquals(3, produtos.size());
        assertEquals("1", produtos.get(0).getImportId());
        assertEquals("1", produtos.get(1).getImportId());
        assertEquals("52", produtos.get(2).getImportId());
        
    }
    
    @Test
    public void testFiltrarProdutosExistentesEVinculados() {
        TestAux aux = new TestAux();
        List<ProdutoIMP> produtos = new ArrayList<>();
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
    }
}

class TestAux {
    private final String sistema = "TEST";
    private final String loja = "1";
    
    public ProdutoIMP impForTest(String id, String descricao, String ean) {
        ProdutoIMP imp = new ProdutoIMP();
        imp.setImportSistema(sistema);
        imp.setImportLoja(loja);
        imp.setImportId(id);
        imp.setDescricaoCompleta(descricao);
        imp.setDescricaoGondola(descricao);
        imp.setDescricaoReduzida(descricao);
        imp.setEan(ean);
        return imp;
    }
}