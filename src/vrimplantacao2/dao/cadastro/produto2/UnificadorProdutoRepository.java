package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
class UnificadorProdutoRepository implements Organizador.OrganizadorNotifier {
    
    private final ProdutoRepositoryProvider provider;   
    private final ProdutoConverter converter;
    private Map<String, Integer> codant;
    private Map<Long, Integer> produtosPorEan;

    public UnificadorProdutoRepository(ProdutoRepositoryProvider provider) {
        this.provider = provider;
        this.converter = new ProdutoConverter(provider);
    }
    
    public void unificar(List<ProdutoIMP> produtos) throws Exception {
        this.codant = provider.anterior().getAnterioresIncluindoComCodigoAtualNull();
        this.produtosPorEan = provider.automacao().getProdutosByEan();
        
        produtos = new Organizador(this).organizarListagem(produtos);
        produtos = filtrarEansValidosParaUnificacao(produtos);
        produtos = filtrarProdutosExistentesEVinculados(produtos);
        System.gc();
        
        List<ProdutoIMP> produtosExistentesComEansExistentes = filtrarProdutosExistentesComEansExistentes(produtos);        
        List<ProdutoIMP> produtosExistentesEansNovos = filtrarProdutosExistentesComEansNovos(produtos);
        List<ProdutoIMP> produtosNaoExistentes = filtrarProdutosNaoExistentes(produtos);
        List<ProdutoIMP> produtosNaoEncontrados = filtrarProdutosNaoEncontrados(produtos);
        System.gc();        
        
        for (ProdutoIMP imp: produtosExistentesComEansExistentes) {
            Integer idProduto = produtosPorEan.get(Utils.stringToLong(imp.getEan()));
            final boolean anteriorExistentePoremSemCodigoAtual = codant.containsKey(imp.getImportId());
            ProdutoAnteriorVO anterior = converter.converterImpEmAnterior(imp);
            
            if (anteriorExistentePoremSemCodigoAtual) {
                provider.anterior().alterar(anterior);
            } else {
                provider.anterior().salvar(anterior);
            }            
        }
    }
    
    List<ProdutoIMP> filtrarEansValidosParaUnificacao(List<ProdutoIMP> produtos) {
        List<ProdutoIMP> result = new ArrayList<>();
        for (ProdutoIMP imp: produtos) {
            long ean = Utils.stringToLong(imp.getEan(), -2);
            if (ean > 999999) {
                result.add(imp);
            }
        }
        return result;
    }
    
    List<ProdutoIMP> filtrarProdutosExistentesEVinculados(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    List<ProdutoIMP> filtrarProdutosExistentesComEansExistentes(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    List<ProdutoIMP> filtrarProdutosExistentesComEansNovos(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    List<ProdutoIMP> filtrarProdutosNaoExistentes(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    List<ProdutoIMP> filtrarProdutosNaoEncontrados(List<ProdutoIMP> produtos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setNotify(String message, int count) throws Exception {
        //TODO: Colocar os eventos da tela.
    }
    
}
