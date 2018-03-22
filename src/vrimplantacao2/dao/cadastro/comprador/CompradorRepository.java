package vrimplantacao2.dao.cadastro.comprador;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.vo.cadastro.comprador.CompradorAnteriorVO;
import vrimplantacao2.vo.cadastro.comprador.CompradorVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.CompradorIMP;

/**
 * 
 * @author Leandro
 */
public class CompradorRepository {
    
    private static final Logger LOG = Logger.getLogger(CompradorRepository.class.getName());
    
    private final CompradorRepositoryProvider provider;

    public CompradorRepository(CompradorRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(List<CompradorIMP> compradores) throws Exception {
        provider.setMessage("Compradores...Carregandos dados...");
        
        Map<String, CompradorAnteriorVO> anteriores = provider.getAnteriores();
        IDStack idsVagos = provider.getIDsVagos();
        
        provider.setMessage("Compradores...Gravando dados...", compradores.size());
        
        provider.begin();
        try {
            
            for (CompradorIMP imp: compradores) {
                
                CompradorAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {                    
                    CompradorVO vo = converterComprador(imp); 
                    vo.setId((int) idsVagos.pop(imp.getId()));
                    provider.gravar(vo);
                    
                    anterior = converterAnterior(imp);
                    anterior.setCodigoAtual(vo.getId());
                    provider.gravar(anterior);
                                        
                    anteriores.put(imp.getId(), anterior);                    
                }
                
                provider.setMessage();
            }
            
            provider.commit();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro ao importar", e);
            provider.rollback();
            throw e;
        }
    }

    public CompradorVO converterComprador(CompradorIMP imp) {
        CompradorVO vo = new CompradorVO();        
        vo.setNome(imp.getDescricao());
        vo.setSituacaoCadastro(SituacaoCadastro.ATIVO);        
        return vo;
    }

    public CompradorAnteriorVO converterAnterior(CompradorIMP imp) {
        CompradorAnteriorVO ant = new CompradorAnteriorVO();
        ant.setSistema(provider.getSistema());
        ant.setLoja(provider.getLoja());
        ant.setId(imp.getId());
        ant.setDescricao(imp.getDescricao());
        return ant;
    }
    
}
