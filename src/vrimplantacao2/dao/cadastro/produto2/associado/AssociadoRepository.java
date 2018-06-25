package vrimplantacao2.dao.cadastro.produto2.associado;

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
            LOG.info("Carregando os associados existentes");
            Map<Integer, AssociadoVO> associadosExistentes = provider.getAssociadosExistentes();
            
            provider.setStatus("Associados...gravando...", associados.size());
            LOG.info("Iniciando gravação dos associados");
            
            for (AssociadoIMP imp: associados) {
                
                Integer produtoPai = produtos.get(imp.getId());
                
                //Verifica a existência do produto pai.
                if (produtoPai != null) {
                    
                    AssociadoVO vo = associadosExistentes.get(produtoPai);
                    if (vo == null) {
                        vo = new AssociadoVO();
                        
                        vo.setIdProduto(produtoPai);
                        vo.setQtdEmbalagem(imp.getQtdEmbalagem());
                        
                        provider.gravar(vo);
                        associadosExistentes.put(vo.getId(), vo);
                        LOG.finest("Produto pai gravado com sucesso: " + vo.getId());
                    } else {                        
                        provider.atualizar(vo, opt);
                        LOG.finest("Produto pai atualizado com sucesso: " + vo.getId());
                    }
                    
                    for (AssociadoItemVO item: imp.getItens()) {
                        Integer produtoFilho = produtos.get(item.getProdutoId());
                        //Verifica a existencia do filho
                        if (produtoFilho != null) {
                            
                            AssociadoItemVO vItem = vo.getItens().get(produtoFilho);
                            if (vItem == null) {
                                
                                vItem = converterItem(item);
                                vItem.setIdAssociado(vo.getId());
                                vItem.setIdProduto(produtoFilho);
                                provider.gravar(vItem);
                                vo.getItens().put(vItem.getId(), vItem);
                                
                            }
                            
                        } else {
                            LOG.warning(imp.getId() + " '" + imp.getDescricao() + " -> Produto filho " + item.getProdutoId() + " '" + item.getDescricao() + "' não foi encontrado!");
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

    private AssociadoItemVO converterItem(AssociadoItemVO item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
