package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
class UnificadorProdutoRepository implements Organizador.OrganizadorNotifier {
    
    private final ProdutoRepositoryProvider provider;   
    private final ProdutoConverter converter;
    private final Map<String, Integer> codant;
    private final Map<Long, Integer> produtosPorEan;
    private final MultiMap<String, Integer> codigosAnterioresIdEan;

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
        
        List<ProdutoIMP> produtosComEanInvalido                 = filtrarProdutosComEanInvalido(produtos);
        List<ProdutoIMP> produtosVinculadosComNovosEans         = filtrarProdutosVinculadosComNovosEans(produtos);
        List<ProdutoIMP> produtosNaoVinculadosComEansExistentes = filtrarProdutosNaoVinculadosComEansExistentes(produtos);        
        List<ProdutoIMP> produtosNaoVinculadosComEansNovos      = filtrarProdutosNaoVinculadosComEansNovos(produtos);
        System.gc();        
        
        for (ProdutoIMP imp: produtosNaoVinculadosComEansExistentes) {
            Integer idProduto = produtosPorEan.get(Utils.stringToLong(imp.getEan()));
            final boolean anteriorExistentePoremSemCodigoAtual = isProdutoVinculadoNaCodAnt(imp);
            ProdutoAnteriorVO anterior = converter.converterImpEmAnterior(imp);
            
            if (anteriorExistentePoremSemCodigoAtual) {
                provider.anterior().alterar(anterior);
            } else {
                provider.anterior().salvar(anterior);
            }            
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
