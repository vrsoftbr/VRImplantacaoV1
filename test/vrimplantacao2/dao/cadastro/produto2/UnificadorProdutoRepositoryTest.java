package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.utils.multimap.MultiMap;
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
        doAnswer(new Answer<Void>() {
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
    public void filtrarEansValidosParaUnificacao() throws Exception {        
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
    public void testFiltrarProdutosEEansExistentes() throws Exception {
        when(provider.anterior().getAnterioresPorIdEan()).then(
                new Answer<MultiMap<String, Integer>>() {
                    @Override
                    public MultiMap<String, Integer> answer(InvocationOnMock invocation) throws Throwable {
                        MultiMap<String, Integer> mp = new MultiMap<>();
                        mp.put(1, "1","7891000100103");
                        mp.put(2, "52","12345678");
                        mp.put(3, "145","78965412");
                        mp.put(4, "200","85236974115987");
                        return mp;
                    }
                }
        );
        
        TestAux aux = new TestAux();
        List<ProdutoIMP> produtos = new ArrayList<>();
        
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("52", "CANETA BIC", "12345678"));
        produtos.add(aux.impForTest("145", "ALFACE", "78965412"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        produtos.add(aux.impForTest("147", "LEITE PARMALAT", "7894569874569"));
        produtos.add(aux.impForTest("189", "REQUEIJAO", "4569873579514"));
        produtos.add(aux.impForTest("200", "ACUCAR", "85236974115987"));
        
        produtos = new UnificadorProdutoRepository(provider).filtrarProdutosEEansJaMapeados(produtos);
        
        assertEquals(4, produtos.size());
        assertEquals("1", produtos.get(0).getImportId());
        assertEquals("17891000100103", produtos.get(0).getEan());
        assertEquals("146", produtos.get(1).getImportId());
        assertEquals("147", produtos.get(2).getImportId());
        assertEquals("189", produtos.get(3).getImportId());
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