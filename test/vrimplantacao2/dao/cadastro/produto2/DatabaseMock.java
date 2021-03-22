package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;

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