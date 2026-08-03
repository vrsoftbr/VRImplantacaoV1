package vrimplantacao2.dao.cadastro.produtosimilar;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.ProdutoSimilarIMP;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarAnteriorVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemAnteriorVO;

/**
 *
 * @author Wesley
 */
public class ProdutoSimilarRepository {

    private static final Logger LOG = Logger.getLogger(ProdutoSimilarRepository.class.getName());

    private final ProdutoSimilarRepositoryProvider provider;

    public ProdutoSimilarRepository(ProdutoSimilarRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importarProdutoSimilar(List<ProdutoSimilarIMP> produtosSimilares) throws Exception {

        provider.begin();
        try {
            provider.createTable();

            provider.setStatus("Produtos Similares...carregando listagens...");
            LOG.info("Carregando produtos anteriores");
            Map<String, Integer> produtos = provider.getProdutosAnteriores();
            
            LOG.info("Carregando os produtos similares importados anteriormente");
            MultiMap<String, ProdutoSimilarAnteriorVO> produtosSimilaresAnteriores = provider.getAnteriores();
            MultiMap<String, ProdutoSimilarItemAnteriorVO> produtosSimilaresItemAnteriores = provider.getItemsAnteriores();

            provider.setStatus("Produto Similar...gravando...", produtosSimilares.size());
            LOG.info("Iniciando gravação dos produtos similares");

            for (ProdutoSimilarIMP imp : produtosSimilares) {
                
                ProdutoSimilarVO vo;
                ProdutoSimilarAnteriorVO produtoSimilarAnterior;
                ProdutoSimilarItemAnteriorVO produtoSimilarItemAnterior;

                //Verifica a existência do produto na tabela codant_produto.
                Integer produto = produtos.get(imp.getIdProduto());

                //TODO: Incluir uma rotina que verifica os itens pelo EAN.
                if (produto != null) {

                    produtoSimilarAnterior = produtosSimilaresAnteriores.get(
                            provider.getSistema(),
                            provider.getLoja(),
                            imp.getIdProdutoSimilar()
                    );
                    
                    if (produtoSimilarAnterior == null) {
                        
                        //GRAVA PRODUTO SIMILAR NO VR CASO NAO EXISTA.
                        vo = new ProdutoSimilarVO();
                        vo.setDescricao(imp.getDescricaoTitulo());
                        vo.setIdSituacaoCadastro(imp.isAtivo() ? 1 : 0);
                        
                        provider.gravar(vo);
                        
                        produtoSimilarAnterior = converterAnterior(imp);
                        produtoSimilarAnterior.setCodigoAtual(vo);
                        produtoSimilarAnterior.setObservacaoImportacao("PRODUTO SIMILAR INSERIDO COMO NOVO");
                        
                        gravarProdutoSimilarAnterior(produtoSimilarAnterior);
                        
                        produtosSimilaresAnteriores.put(
                                produtoSimilarAnterior,
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getIdProdutoSimilar());
                    } else {
                        
                        //VERIFICA SE VEM A DESCRICAO
                        vo = produtoSimilarAnterior.getCodigoAtual();
                    }

                    //IMPORTAR ITEM SIMILAR.
                    produtoSimilarItemAnterior = produtosSimilaresItemAnteriores.get(
                            provider.getSistema(),
                            provider.getLoja(),
                            imp.getIdProdutoSimilarItem()
                    );
                    
                    if (produtoSimilarItemAnterior == null) {
                        
                        ProdutoSimilarItemVO vItem = new ProdutoSimilarItemVO();
                        vItem.setIdProdutoSimilar(vo);
                        vItem.setIdProduto(produto);
                        
                        provider.gravar(vItem);
                        
                        produtoSimilarItemAnterior = converterItemAnterior(imp);
                        produtoSimilarItemAnterior.setCodigoAtual(vItem);
                        produtoSimilarItemAnterior.setCodigoAtualProdutoSimilar(vo);
                        
                        ProdutoVO prod = new ProdutoVO();
                        prod.setId(produto);
                        produtoSimilarItemAnterior.setCodigoAtualProduto(prod);
                        
                        produtoSimilarItemAnterior.setObservacaoImportacao("\"PRODUTO SIMILAR ITEM INSERIDO COMO NOVO\"");
                        
                        gravarProdutoSimilarItemAnterior(produtoSimilarItemAnterior);
                        
                        produtosSimilaresItemAnteriores.put(
                                produtoSimilarItemAnterior, 
                                provider.getSistema(),
                                provider.getLoja(),
                                imp.getIdProdutoSimilarItem()
                        );
                        
                    } else {
                        //POSSIBILIDADE DE GRAVAR CODANT ITEM SEM CODIGO ATUAL 
//                            LOG.warning(imp.getId() + " '" + imp.getDescricao() + " -> Produto filho " + imp.getProdutoAssociadoId() + " '" + imp.getDescricaoProdutoAssociado() + "' não foi encontrado!");
                    }

                } else {
                    //POSSIBILIDADE DE GRAVAR CODANT SEM CODIGO ATUAL
//                    LOG.warning("Produto pai " + imp.getId() + " '" + imp.getDescricao() + "' não foi encontrado!");
                }

                provider.setStatus();
            }

            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }

    }

    public ProdutoSimilarAnteriorVO converterAnterior(ProdutoSimilarIMP imp) {
        ProdutoSimilarAnteriorVO ant = new ProdutoSimilarAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(imp.getIdProdutoSimilar());
        ant.setDescricao(imp.getDescricaoTitulo());

        return ant;
    }

    public ProdutoSimilarItemAnteriorVO converterItemAnterior(ProdutoSimilarIMP imp) {
        ProdutoSimilarItemAnteriorVO ant = new ProdutoSimilarItemAnteriorVO();
        ant.setImportSistema(provider.getSistema());
        ant.setImportLoja(provider.getLoja());
        ant.setImportId(imp.getIdProdutoSimilarItem());
        ant.setImportIdProdutoSimilar(imp.getIdProdutoSimilar());
        ant.setImportIdProduto(imp.getIdProduto());

        return ant;
    }

    public void gravarProdutoSimilarAnterior(ProdutoSimilarAnteriorVO anterior) throws Exception {
        provider.gravarProdutoSimilarAnterior(anterior);
    }

    public void gravarProdutoSimilarItemAnterior(ProdutoSimilarItemAnteriorVO anterior) throws Exception {
        provider.gravarProdutoSimilarItemAnterior(anterior);
    }
}
