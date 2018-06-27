package vrimplantacao2.dao.cadastro.produto2.associado;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoVO;
import vrimplantacao2.vo.importacao.AssociadoIMP;

/**
 *
 * @author Leandro
 */
public class AssociadoRepository {
    
    private static final Logger LOG = Logger.getLogger(AssociadoRepository.class.getName());
    
    private final AssociadoRepositoryProvider provider;

    public AssociadoRepository(AssociadoRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importarAssociado(List<AssociadoIMP> associados, Set<OpcaoAssociado> opt) throws Exception {
        
        provider.begin();
        try {
        
            provider.setStatus("Associados...carregando listagens...");
            LOG.info("Carregando produtos anteriores");                
            Map<String, Integer> produtos = provider.getProdutosAnteriores();
            LOG.info("Carregando os associados existentes e seus itens");
            Map<Integer, AssociadoVO> associadosExistentes = provider.getAssociadosExistentes();
            LOG.info("Carregando produtos ativos");
            Set<Integer> produtosAtivos;
            
            boolean somenteAtivos = opt.contains(OpcaoAssociado.IMP_SOMENTE_ATIVOS);
            if (somenteAtivos) {
                produtosAtivos = provider.getProdutosAtivos();
            } else {
                produtosAtivos = new HashSet<>();
            }
            
            provider.setStatus("Associados...gravando...", associados.size());
            LOG.info("Iniciando gravação dos associados");            
            
            //Para cada associado, execute.
            for (AssociadoIMP imp: associados) {
                //Verifica a existência do produto pai na tabela codant_produto.
                Integer produtoPai = produtos.get(imp.getId());
                //TODO: Incluir uma rotina que verifica os itens pelo EAN.
                if (produtoPai != null) {
                    if (!somenteAtivos || (somenteAtivos && produtosAtivos.contains(produtoPai))) {
                        //Verifica se existe algum associado com este produto.
                        AssociadoVO vo = associadosExistentes.get(produtoPai);
                        if (vo == null) {
                            //Se não existir cria um novo associado e o cadastra no VR.
                            vo = new AssociadoVO();                        
                            vo.setIdProduto(produtoPai);
                            vo.setQtdEmbalagem(imp.getQtdEmbalagem());
                            provider.gravar(vo);
                            associadosExistentes.put(vo.getIdProduto(), vo);
                            LOG.finest("Produto pai gravado com sucesso: " + vo.getId());
                        }

                        //Importa os itens do associados.

                        //Verifica a existencia do produto filho na tabela codant_produto.
                        Integer produtoFilho = produtos.get(imp.getProdutoAssociadoId());
                        //TODO: Incluir uma rotina que verifica os itens pelo EAN.
                        if (produtoFilho != null) {
                            //Se o produto filho existir, verifica se já está cadastrado no associado.
                            AssociadoItemVO vItem = vo.getItens().get(produtoFilho);
                            if (vItem == null) {
                                //Se não estiver cadastrado, converte e cadastra.
                                vItem = new AssociadoItemVO();
                                vItem.setIdAssociado(vo.getId());
                                vItem.setIdProduto(produtoFilho);
                                vItem.setQtdEmbalagem(1);
                                vItem.setAplicaEstoque(true);
                                vItem.setAplicaCusto(false);
                                vItem.setAplicaPreco(false);
                                vItem.setPercentualCustoEstoque(0);
                                vItem.setPercentualPreco(0);
                                provider.gravar(vItem);
                                vo.getItens().put(vItem.getIdProduto(), vItem);                                
                            }

                            //Gera a inversão se não existir
                            if (opt.contains(OpcaoAssociado.IMP_INVERTER)) { 

                                AssociadoVO invertVo = associadosExistentes.get(vItem.getIdProduto());

                                //Verifica se o associado já existe.
                                if (invertVo == null) {                                    
                                    //Coloca o item do associado como pai.
                                    invertVo = new AssociadoVO();
                                    invertVo.setIdProduto(vItem.getIdProduto());
                                    invertVo.setQtdEmbalagem(1);
                                    provider.gravar(invertVo);
                                    associadosExistentes.put(invertVo.getIdProduto(), invertVo);
                                }

                                if (!invertVo.getItens().containsKey(vo.getIdProduto())) {
                                    //Coloca o pai como filho.
                                    AssociadoItemVO invertItem = new AssociadoItemVO();
                                    invertItem.setIdAssociado(invertVo.getId());
                                    invertItem.setIdProduto(vo.getIdProduto());
                                    invertItem.setQtdEmbalagem(vo.getQtdEmbalagem());
                                    invertItem.setAplicaEstoque(false);
                                    invertItem.setAplicaCusto(true);
                                    invertItem.setAplicaPreco(true);
                                    invertItem.setPercentualCustoEstoque(imp.getPercentualCusto());
                                    invertItem.setPercentualPreco(imp.getPercentualPreco());
                                    provider.gravar(invertItem);
                                    invertVo.getItens().put(invertItem.getIdProduto(), invertItem);                                        
                                }

                            }

                        } else {
                            LOG.warning(imp.getId() + " '" + imp.getDescricao() + " -> Produto filho " + imp.getProdutoAssociadoId() + " '" + imp.getDescricaoProdutoAssociado() + "' não foi encontrado!");
                        }                    
                    }
                                        
                } else {
                    LOG.warning("Produto pai " + imp.getId() + " '" + imp.getDescricao() + "' não foi encontrado!");
                }                
            
                provider.setStatus();
            }
            
            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
        
    }
    
}
