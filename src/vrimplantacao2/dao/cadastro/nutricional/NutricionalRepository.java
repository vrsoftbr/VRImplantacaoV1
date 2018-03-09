package vrimplantacao2.dao.cadastro.nutricional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.nutricional.NutricionalAnteriorVO;
import vrimplantacao2.vo.importacao.NutricionalIMP;

/**
 *
 * @author Leandro
 */
public class NutricionalRepository {
    
    private static final Logger LOG = Logger.getLogger(NutricionalRepository.class.getName());
    
    private NutricionalRepositoryProvider provider;

    public NutricionalRepository(NutricionalRepositoryProvider repository) {
        this.provider = repository;
    }

    public void importar(List<NutricionalIMP> nutricionais, Set<OpcaoNutricional> opt) throws Exception {
        provider.setStatus("Nutricionais...Carregando dados");
        LOG.fine("Carregando os preliminares necessários");
        Map<String, NutricionalAnteriorVO> anteriores = provider.getAnteriores();
        Map<String, Integer> produtos = provider.getProdutos();
        MultiMap<Integer, Void> nutricionaisFilizola = null;
        
        if (opt.contains(OpcaoNutricional.FILIZOLA)) {
            nutricionaisFilizola = provider.getNutricionaisFilizola();
        }
        
        provider.begin();
        try {
        
            for (NutricionalIMP imp: nutricionais) {
                LOG.finer("Nutricional: " + imp.getId() + " - " + imp.getDescricao());
                NutricionalAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {
                    
                    anterior = converterAnterior(imp);
                    
                    if (opt.contains(OpcaoNutricional.FILIZOLA)) {
                        NutricionalFilizolaVO vo = converterNutricionalFilizola(imp);
                        
                        provider.gravar(vo);
                        
                        anterior.setCodigoAtualFilizola(vo.getId());
                        LOG.finest("Nutricional Filizola gravado");
                    }
                    
                    if (!opt.isEmpty()) {
                        provider.gravar(anterior);
                        anteriores.put(imp.getId(), anterior);
                        LOG.finest("Nutricional anterior gravado");
                    }
                    
                }
                
                for (String produto: imp.getProdutos()) {
                    Integer idProduto = produtos.get(produto);
                    if (idProduto != null) {
                        if (opt.contains(OpcaoNutricional.FILIZOLA)) {
                            if (!nutricionaisFilizola.containsKey(anterior.getCodigoAtualFilizola(), idProduto)) {
                                provider.gravarItem(anterior.getCodigoAtualFilizola(), idProduto);    
                                nutricionaisFilizola.put(null, anterior.getCodigoAtualFilizola(), idProduto);
                                LOG.finest("ID Produto " + idProduto + " gravado no nutricional " + anterior.getCodigoAtualFilizola()); 
                            }
                        }
                    }
                }
                
            }
            
            provider.commit();
            LOG.fine("Nutricionais importados com sucesso!");
        } catch (Exception e) {
            provider.rollback();
            LOG.log(Level.SEVERE, "Erro ao importar os nutricionais", e);
            throw e;            
        }
        
    }

    public NutricionalAnteriorVO converterAnterior(NutricionalIMP imp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public NutricionalFilizolaVO converterNutricionalFilizola(NutricionalIMP imp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
