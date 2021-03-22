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
        
        ArrayList<ProdutoIMP> produtos = new ArrayList<>();
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("2", "ACEM", "345"));
        produtos.add(aux.impForTest("5", "PICANHA", "45"));
        produtos.add(aux.impForTest("52", "CANETA BIC", "12345678"));
        
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("52", "CANETA BIC", "12345678"));
        produtos.add(aux.impForTest("145", "ALFACE", "78965412"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        produtos.add(aux.impForTest("147", "LEITE PARMALAT", "7894569874569"));
        produtos.add(aux.impForTest("189", "REQUEIJAO", "4569873579514"));
        produtos.add(aux.impForTest("200", "ACUCAR", "85236974115987"));
        
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        produtos.add(aux.impForTest("147", "LEITE PARMALAT", "7894569874569"));
        produtos.add(aux.impForTest("189", "REQUEIJAO", "4569873579514"));
        produtos.add(aux.impForTest("10", "TESTE1", "0007896541"));
        produtos.add(aux.impForTest("1000", "TESTE2", "0009874563"));
        
        repository.unificar(produtos);        
        
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
        produtos.add(aux.impForTest("145", "ALFACE", "78965412"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        produtos.add(aux.impForTest("147", "LEITE PARMALAT", "7894569874569"));
        produtos.add(aux.impForTest("189", "REQUEIJAO", "4569873579514"));
        produtos.add(aux.impForTest("200", "ACUCAR", "85236974115987"));
        produtos.add(aux.impForTest("10", "TESTE1", "0007896541"));
        produtos.add(aux.impForTest("1000", "TESTE2", "0009874563"));
        
        UnificadorProdutoRepository rep = new UnificadorProdutoRepository(provider);
        List<ProdutoIMP> invalidos = rep.filtrarProdutosComEanInvalido(produtos);
        
        assertEquals(10, produtos.size());
        assertEquals("1", produtos.get(0).getImportId());
        assertEquals("1", produtos.get(1).getImportId());
        assertEquals("52", produtos.get(2).getImportId());
        assertEquals("145", produtos.get(3).getImportId());
        assertEquals("146", produtos.get(4).getImportId());
        assertEquals("147", produtos.get(5).getImportId());
        assertEquals("189", produtos.get(6).getImportId());
        assertEquals("200", produtos.get(7).getImportId());
        assertEquals("10", produtos.get(8).getImportId());
        assertEquals("1000", produtos.get(9).getImportId());
        
        assertEquals(2, invalidos.size());
        assertEquals("2", invalidos.get(0).getImportId());
        assertEquals("5", invalidos.get(1).getImportId());
        
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
        produtos.add(aux.impForTest("10", "TESTE1", "0007896541"));
        produtos.add(aux.impForTest("1000", "TESTE2", "0009874563"));
        
        produtos = new UnificadorProdutoRepository(provider).filtrarProdutosEEansJaMapeados(produtos);
        
        assertEquals(6, produtos.size());
        assertEquals("1", produtos.get(0).getImportId());
        assertEquals("17891000100103", produtos.get(0).getEan());
        assertEquals("146", produtos.get(1).getImportId());
        assertEquals("147", produtos.get(2).getImportId());
        assertEquals("189", produtos.get(3).getImportId());
        assertEquals("10", produtos.get(4).getImportId());
        assertEquals("1000", produtos.get(5).getImportId());
    }
    
    @Test
    public void testFiltrarProdutosVinculadosComNovosEans() throws Exception {
        
        when(provider.anterior().getAnterioresIncluindoComCodigoAtualNull()).then(
                new Answer<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> answer(InvocationOnMock invocation) throws Throwable {
                        Map<String, Integer> mp = new HashMap<>();
                        mp.put("1", 1);
                        mp.put("52", 2);
                        mp.put("145", 3);
                        mp.put("10", 5);
                        mp.put("200", 4);
                        return mp;
                    }
                }
        );
        when(provider.automacao().getProdutosByEan()).then(
                new Answer<Map<Long, Integer>>() {
                    @Override
                    public Map<Long, Integer> answer(InvocationOnMock invocation) throws Throwable {
                        Map<Long, Integer> mp = new HashMap<>();
                        mp.put(7891000100103L, 1);
                        mp.put(9874563L, 10);
                        return mp;
                    }
                }
        );
        
        TestAux aux = new TestAux();
        List<ProdutoIMP> produtos = new ArrayList<>();
        
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        produtos.add(aux.impForTest("147", "LEITE PARMALAT", "7894569874569"));
        produtos.add(aux.impForTest("189", "REQUEIJAO", "4569873579514"));
        produtos.add(aux.impForTest("10", "TESTE1", "0007896541"));
        produtos.add(aux.impForTest("1000", "TESTE2", "0009874563"));
        
        List<ProdutoIMP> produtosVinculadosComNovosEans = new UnificadorProdutoRepository(provider).filtrarProdutosVinculadosComNovosEans(produtos);
        
        assertEquals(4, produtos.size());
        assertEquals(2, produtosVinculadosComNovosEans.size());
        assertEquals("1", produtosVinculadosComNovosEans.get(0).getImportId());
        assertEquals("17891000100103", produtosVinculadosComNovosEans.get(0).getEan());
        assertEquals("10", produtosVinculadosComNovosEans.get(1).getImportId());
        assertEquals("0007896541", produtosVinculadosComNovosEans.get(1).getEan());
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