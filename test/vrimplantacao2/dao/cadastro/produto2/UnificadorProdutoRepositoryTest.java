package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

@RunWith(MockitoJUnitRunner.class)
public class UnificadorProdutoRepositoryTest {
    
    public UnificadorProdutoRepositoryTest() {
    }
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProdutoRepositoryProvider provider;
    
    @Before
    public void setProvider() {
        when(provider.getSistema()).thenReturn("TEST");
        when(provider.getLoja()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);
    }

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
    public void filtrarProdutosComEanInvalido() throws Exception {        
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
    public void testGravarProdutosComEanInvalido() throws Exception {
        
        TestAux aux = new TestAux();
        DatabaseMock databaseMock = new DatabaseMock(provider);
        databaseMock.addProdutoAnterior("6", "TESTE", 1);
        
        List<ProdutoIMP> produtos = new ArrayList<>();
        produtos.add(aux.impForTest("1", "MOCA", "7891000100103"));
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("2", "ACEM", "345"));
        produtos.add(aux.impForTest("5", "PICANHA", "45"));
        produtos.add(aux.impForTest("5", "PICANHA", "698"));
        produtos.add(aux.impForTest("6", "TESTE", "789452"));
        produtos.add(aux.impForTest("52", "CANETA BIC", "12345678"));
        produtos.add(aux.impForTest("145", "ALFACE", "78965412"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        
        assertEquals(1, databaseMock.implantacao_codant_produto.size());
        assertEquals(0, databaseMock.implantacao_codant_ean.size());
        assertEquals("6", databaseMock.implantacao_codant_produto.get(0).getImportId());
        
        new UnificadorProdutoRepository(provider).gravarProdutosComEanInvalido(produtos);
        
        assertEquals(3, databaseMock.implantacao_codant_produto.size());
        assertEquals("6", databaseMock.implantacao_codant_produto.get(0).getImportId());
        assertEquals("2", databaseMock.implantacao_codant_produto.get(1).getImportId());
        assertEquals("5", databaseMock.implantacao_codant_produto.get(2).getImportId());
        
        assertEquals(4, databaseMock.implantacao_codant_ean.size());
        assertEquals("345", databaseMock.implantacao_codant_ean.get(0).getEan());
        assertEquals("45", databaseMock.implantacao_codant_ean.get(1).getEan());
        assertEquals("698", databaseMock.implantacao_codant_ean.get(2).getEan());
        assertEquals("789452", databaseMock.implantacao_codant_ean.get(3).getEan());
        
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
        
        DatabaseMock db = new DatabaseMock(provider);
        db.addProdutoAnterior("1", "", 1);
        db.addProdutoAnterior("52", "", 2);
        db.addProdutoAnterior("145", "", 3);
        db.addProdutoAnterior("10", "", 4);
        db.addProdutoAnterior("200", "", 5);        
        db.addProdutoAutomacao(1, 7891000100103L);
        db.addProdutoAutomacao(10, 9874563L);
        
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
    
    @Test
    public void testGravarProdutoVinculadosComNovosEans() throws Exception {
        
        DatabaseMock db = new DatabaseMock(provider);
        db.addProdutoAnterior("1", "", 1);
        db.addProdutoAnterior("52", "", 2);
        db.addProdutoAnterior("145", "", 3);
        db.addProdutoAnterior("10", "", 4);
        db.addProdutoAnterior("200", "", 5);        
        db.addProdutoAutomacao(1, 7891000100103L);
        db.addProdutoAutomacao(10, 9874563L);
        
        TestAux aux = new TestAux();
        List<ProdutoIMP> produtos = new ArrayList<>();
        
        produtos.add(aux.impForTest("1", "MOCA", "17891000100103"));
        produtos.add(aux.impForTest("146", "COUVE", "789456321"));
        produtos.add(aux.impForTest("147", "LEITE PARMALAT", "7894569874569"));
        produtos.add(aux.impForTest("189", "REQUEIJAO", "4569873579514"));
        produtos.add(aux.impForTest("10", "TESTE1", "0007896541"));
        produtos.add(aux.impForTest("1000", "TESTE2", "0009874563"));
        
        assertEquals(5, db.implantacao_codant_produto.size());
        assertEquals(2, db.public_produtoautomacao.size());
        assertEquals(0, db.implantacao_codant_ean.size());
        new UnificadorProdutoRepository(provider).gravarProdutosVinculadosComNovosEans(produtos);
        assertEquals(5, db.implantacao_codant_produto.size());
        assertEquals(4, db.public_produtoautomacao.size());
        assertEquals(2, db.implantacao_codant_ean.size());
        
        assertEquals("1", db.implantacao_codant_ean.get(0).getImportId());
        assertEquals("17891000100103", db.implantacao_codant_ean.get(0).getEan());
        assertEquals("10", db.implantacao_codant_ean.get(1).getImportId());
        assertEquals("0007896541", db.implantacao_codant_ean.get(1).getEan());
        
        assertEquals(17891000100103L, db.public_produtoautomacao.get(2).getCodigoBarras());
        assertEquals(7896541L, db.public_produtoautomacao.get(3).getCodigoBarras());
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

class DatabaseMock {

    private final ProdutoRepositoryProvider provider;
    private final ProdutoRepositoryProvider.Anterior anterior;
    private final ProdutoRepositoryProvider.EanAnterior eanAnterior;
    private final ProdutoRepositoryProvider.Automacao automacao;

    List<ProdutoAnteriorVO> implantacao_codant_produto = new ArrayList<>();
    List<ProdutoAnteriorEanVO> implantacao_codant_ean = new ArrayList<>();
    List<ProdutoAutomacaoVO> public_produtoautomacao = new ArrayList<>();

    public DatabaseMock(ProdutoRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.anterior = provider.anterior();
        this.eanAnterior = provider.eanAnterior();
        this.automacao = provider.automacao();
        setAnteriorMock();
        setEanAnteriorMock();
        setAutomacaoMock();
    }

    private void setAnteriorMock() throws Exception {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                implantacao_codant_produto.add(invocation.getArgumentAt(0, ProdutoAnteriorVO.class));
                return null;
            }
        }).when(anterior).salvar(any(ProdutoAnteriorVO.class));

        doAnswer(new Answer<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Integer> mp = new HashMap<>();
                for (ProdutoAnteriorVO ant: DatabaseMock.this.implantacao_codant_produto) {
                    mp.put(
                            ant.getImportId(),
                            (ant.getCodigoAtual() == null ? null : ant.getCodigoAtual().getId())
                    );
                }
                return mp;
            }
        }).when(anterior).getAnterioresIncluindoComCodigoAtualNull();
    }

    private void setEanAnteriorMock() throws Exception {
        when(anterior.getAnterioresPorIdEan()).thenReturn(new MultiMap<String, Integer>());
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                DatabaseMock.this.implantacao_codant_ean.add(invocation.getArgumentAt(0, ProdutoAnteriorEanVO.class));
                return null;
            }
        }).when(eanAnterior).salvar(any(ProdutoAnteriorEanVO.class));
    }
    
    private void setAutomacaoMock() throws Exception {
        doAnswer(new Answer<Map<Long, Integer>>() {
            @Override
            public Map<Long, Integer> answer(InvocationOnMock invocation) throws Throwable {
                Map<Long, Integer> mp = new HashMap<>();
                for (ProdutoAutomacaoVO ean: DatabaseMock.this.public_produtoautomacao) {
                    mp.put(
                            ean.getCodigoBarras(),
                            ean.getProduto().getId()
                    );
                }
                return mp;
            }
        }).when(automacao).getProdutosByEan();
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                DatabaseMock.this.public_produtoautomacao.add(invocation.getArgumentAt(0, ProdutoAutomacaoVO.class));
                return null;
            }
        }).when(automacao).salvar(any(ProdutoAutomacaoVO.class));
    }
    
    public void addProdutoAutomacao(int id, long ean) {
        ProdutoAutomacaoVO produtoAutomacao = new ProdutoAutomacaoVO();
        final ProdutoVO prod = new ProdutoVO();
        prod.setId(id);
        produtoAutomacao.setProduto(prod);
        produtoAutomacao.setCodigoBarras(ean);
        this.public_produtoautomacao.add(produtoAutomacao);
    }

    public void addProdutoAnterior(String id, String descricao, Integer codigoAtual) {
        ProdutoAnteriorVO ant = new ProdutoAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(id);
        ant.setDescricao(descricao);
        ant.setCodigoAtual(codigoAtual);
        this.implantacao_codant_produto.add(ant);
    }
}