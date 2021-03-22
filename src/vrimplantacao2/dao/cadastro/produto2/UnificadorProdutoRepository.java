package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
class UnificadorProdutoRepository implements Organizador.OrganizadorNotifier {
    
    private final ProdutoRepositoryProvider provider;   
    private final ProdutoConverter converter;
    
    final Map<String, Integer> codant;
    final Map<Long, Integer> produtosPorEan;
    final MultiMap<String, Integer> codigosAnterioresIdEan;

    public UnificadorProdutoRepository(ProdutoRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.converter = new ProdutoConverter(provider);
        
        this.codant = provider.anterior().getAnterioresIncluindoComCodigoAtualNull();
        this.produtosPorEan = provider.automacao().getProdutosByEan();
        this.codigosAnterioresIdEan = provider.anterior().getAnterioresPorIdEan();
    }
    
    public void unificar(List<ProdutoIMP> produtos) throws Exception {        
        produtos = new Organizador(this).organizarListagem(produtos);
        produtos = filtrarProdutosEEansJaMapeados(produtos);
        System.gc();        
        
        gravarProdutosComEanInvalido(produtos);
        gravarProdutosVinculadosComNovosEans(produtos);
        List<ProdutoIMP> produtosNaoVinculadosComEansExistentes = filtrarProdutosNaoVinculadosComEansExistentes(produtos);        
        List<ProdutoIMP> produtosNaoVinculadosComEansNovos      = filtrarProdutosNaoVinculadosComEansNovos(produtos);
        System.gc();        
        
    }
    
    List<ProdutoIMP> filtrarProdutosEEansJaMapeados(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp: produtos) {
            if (isEanEIdExistenteNaCodAnt(imp))
                continue;
            result.add(imp);
        }
        return result;
    }
    private boolean isEanEIdExistenteNaCodAnt(ProdutoIMP imp) {
        return this.codigosAnterioresIdEan.containsKey(imp.getImportId(), imp.getEan());
    }

    void gravarProdutosComEanInvalido(List<ProdutoIMP> produtos) throws Exception {        
        List<ProdutoIMP> produtosComEanInvalido = filtrarProdutosComEanInvalido(produtos);        
        for (ProdutoIMP imp: produtosComEanInvalido) {
            boolean existeNaCodAnt = this.codant.containsKey(imp.getImportId());
            if (!existeNaCodAnt) {     
                gravarAnterior(imp, null);
            }
            gravarEanAnterior(imp, null);
        }        
    }
    List<ProdutoIMP> filtrarProdutosComEanInvalido(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp: produtos) {
            long ean = Utils.stringToLong(imp.getEan(), -2);
            if (ean > 999999)
                continue;
            result.add(imp);
        }
        produtos.removeAll(result);
        return result;
    }
    void gravarAnterior(ProdutoIMP imp, Integer codigoAtual) throws Exception {
        ProdutoAnteriorVO anterior = this.converter.converterImpEmAnterior(imp);
        provider.anterior().salvar(anterior);
        this.codant.put(anterior.getImportId(), codigoAtual);
    }
    void gravarEanAnterior(ProdutoIMP imp, Integer codigoAtual) throws Exception {
        ProdutoAnteriorEanVO eanAnterior = this.converter.converterAnteriorEAN(imp);
        provider.eanAnterior().salvar(eanAnterior);
        this.codigosAnterioresIdEan.put(codigoAtual, eanAnterior.getImportId(), eanAnterior.getEan());
    }
    
    void gravarProdutosVinculadosComNovosEans(List<ProdutoIMP> produtos) throws Exception {        
        List<ProdutoIMP> produtosVinculadosComNovosEans = filtrarProdutosVinculadosComNovosEans(produtos);
        
        for (ProdutoIMP imp: produtosVinculadosComNovosEans) {
            long ean = Utils.stringToLong(imp.getEan());
            TipoEmbalagem unidade = TipoEmbalagem.getTipoEmbalagem(imp.getTipoEmbalagem());
            ProdutoAutomacaoVO produtoAutomacao = this.converter.converterEAN(imp, ean, unidade);
            int idProduto = this.codant.get(imp.getImportId());
            produtoAutomacao.setProduto(idProduto);
            
            provider.automacao().salvar(produtoAutomacao);
            this.produtosPorEan.put(produtoAutomacao.getCodigoBarras(), produtoAutomacao.getProduto().getId());
            
            gravarEanAnterior(imp, idProduto);
            
        }
    }
    
    List<ProdutoIMP> filtrarProdutosVinculadosComNovosEans(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp: produtos) {            
            if (!isProdutoVinculadoNaCodAnt(imp))
                continue;
            if (isEanExistenteNoVR(imp))
                continue;
            result.add(imp);
        }
        produtos.removeAll(result);
        return result;
    }
    private boolean isProdutoVinculadoNaCodAnt(ProdutoIMP imp) {
        return this.codant.containsKey(imp.getImportId());
    }
    private boolean isEanExistenteNoVR(ProdutoIMP imp) {
        return this.produtosPorEan.containsKey(Utils.stringToLong(imp.getEan()));
    }
    
    List<ProdutoIMP> filtrarProdutosNaoVinculadosComEansExistentes(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    List<ProdutoIMP> filtrarProdutosNaoVinculadosComEansNovos(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNotify(String message, int count) throws Exception {
        //TODO: Colocar os eventos da tela.
    }
    
}
