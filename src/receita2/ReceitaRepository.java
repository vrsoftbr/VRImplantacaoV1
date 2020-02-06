package receita2;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import receita.ReceitaIMP;
import receita.ReceitaItemIMP;
import receita.ReceitaProdutoIMP;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaItemVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaProdutoVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaVO;

/**
 *
 * @author leandro
 */
public class ReceitaRepository {
    
    private static final Logger LOG = Logger.getLogger(ReceitaRepository.class.getName());

    final ReceitaRepositoryProvider provider;
    
    public ReceitaRepository(String sistema, String loja, int lojaVR) throws Exception {
        this.provider = new ReceitaRepositoryProvider(
                sistema, loja, lojaVR
        );
    }

    public void importar(List<ReceitaIMP> receitas) throws Exception {
        provider.setMessage("Receita...Carregando dados...");
        
        provider.begin();
        try {
        
            Map<String, ReceitaAnteriorVO> anteriores = provider.getAnteriores();
            Map<String, Integer> produtos = provider.getProdutos();
            
            provider.setMessage("Receita...Gravando...", receitas.size());
            for (ReceitaIMP imp: receitas) {
                
                ReceitaAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null || anterior.getCodigoAtual() == 0) {
                    
                    ReceitaVO receita = converterReceita(imp);                    
                    provider.incluirReceita(receita);
                    
                    //Se for um registro novo
                    if (anterior == null) {
                        anterior = converterAnterior(imp);
                        anterior.setCodigoAtual(receita.getId());
                        provider.incluirAnterior(anterior);                    
                        anteriores.put(imp.getId(), anterior);
                    } else {
                        //Se já existir, apenas atualiza o registro
                        anterior = converterAnterior(imp);
                        anterior.setCodigoAtual(receita.getId());
                        provider.atualizarAnterior(anterior);
                    }
                    
                }
                
                //Se foi incluso o código anterior
                if (anterior.getCodigoAtual() > 0) {
                    //Itens da receita
                    for (ReceitaItemIMP item: imp.getItens()) {
                        Integer idProduto = produtos.get(item.getIdProduto());

                        //Caso o produto não seja encontrado, loga o erro e vai para o próximo
                        if (idProduto == null) {
                            provider.log(
                                    LOG,
                                    Level.WARNING,
                                    "Receita ID: %s - %s - (Item Receita) Produto '%s' não foi encontrada na codant_produto",
                                    imp.getId(),
                                    imp.getDescricao(),
                                    item.getIdProduto()
                            );
                            continue;
                        }

                        //Se o produto não estiver incluso, inclui um novo
                        if (!anterior.getItens().contains(idProduto)) {
                            ReceitaItemVO i = converterReceitaItem(item);
                            i.setId_receita(anterior.getCodigoAtual());
                            i.setId_produto(idProduto);
                            provider.inserirItem(i);
                            anterior.getItens().add(idProduto);
                        }
                    }

                    //Rendimentos
                    for (ReceitaProdutoIMP prod: imp.getRendimento()) {
                        Integer idProduto = produtos.get(prod.getIdProduto());

                        //Caso o produto não seja encontrado, loga o erro e vai para o próximo
                        if (idProduto == null) {
                            provider.log(
                                    LOG,
                                    Level.WARNING,
                                    "Receita ID: %s - %s - (Item Rendimento) Produto '%s' não foi encontrada na codant_produto",
                                    imp.getId(),
                                    imp.getDescricao(),
                                    prod.getIdProduto()
                            );
                            continue;
                        }

                        //Se o produto não estiver incluso, inclui um novo
                        if (!anterior.getRendimentos().contains(idProduto)) {
                            ReceitaProdutoVO i = converterReceitaProduto(prod);
                            i.setId_receita(anterior.getCodigoAtual());
                            i.setId_produto(idProduto);
                            provider.inserirReceitaProduto(i);
                            anterior.getRendimentos().add(idProduto);
                        }
                    }
                }
                
                provider.setMessage();
            }
            
            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }

    private ReceitaItemVO converterReceitaItem(ReceitaItemIMP imp) {
        ReceitaItemVO vo = new ReceitaItemVO();
        
        vo.setQtdembalagemproduto(imp.getQtdEmbalagemProduto());
        vo.setQtdembalagemreceita(imp.getQtdEmbalagemReceita());
        vo.setFatorconversao(imp.getFatorConversao());
        
        return vo;
    }

    private ReceitaProdutoVO converterReceitaProduto(ReceitaProdutoIMP imp) {
        ReceitaProdutoVO vo = new ReceitaProdutoVO();

        vo.setRendimento(imp.getRendimento());
        
        return vo;
    }

    private ReceitaVO converterReceita(ReceitaIMP imp) {
        ReceitaVO vo = new ReceitaVO();

        vo.setDescricao(imp.getDescricao());
        vo.setFichatecnica(imp.getFichaTecnica());
        vo.setId_situacaocadastro(imp.getSituacaoCadastro().getId());

        return vo;
    }

    private ReceitaAnteriorVO converterAnterior(ReceitaIMP imp) {
        
        ReceitaAnteriorVO ant = new ReceitaAnteriorVO();

        ant.setImportsistema(provider.getSistema());
        ant.setImportloja(provider.getLoja());
        ant.setImportid(imp.getId());
        if (!imp.getRendimento().isEmpty()) {
            ReceitaProdutoIMP get = imp.getRendimento().iterator().next();
            ant.setIdproduto(get.getIdProduto());
            ant.setRendimento(get.getRendimento());
        }
        ant.setDescricao(imp.getDescricao());
        ant.setFichatecnica(imp.getFichaTecnica());
        if (!imp.getItens().isEmpty()) {
            ReceitaItemIMP get = imp.getItens().iterator().next();
            ant.setQtdembalagemreceita(get.getQtdEmbalagemReceita());
            ant.setQtdembalagemproduto(get.getQtdEmbalagemProduto());
        }

        return ant;
        
    }
    
}
