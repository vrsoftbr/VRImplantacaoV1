package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.PdvFuncaoOperadorIMP;
import vrimplantacao2_5.Financeiro.Provider.PdvFuncaoOperadorRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.PdvFuncaoOperadorVO;

public class PdvFuncaoOperadorRepository {
    
    private static final Logger LOG = Logger.getLogger(PdvFuncaoOperadorRepository.class.getName());
    
    private final PdvFuncaoOperadorRepositoryProvider provider;
    
    public PdvFuncaoOperadorRepository(PdvFuncaoOperadorRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public PdvFuncaoOperadorVO converter(PdvFuncaoOperadorIMP imp) {
        PdvFuncaoOperadorVO vo = new PdvFuncaoOperadorVO();
        
        vo.setId(imp.getId());
        vo.setId_loja(imp.getId_loja());
        vo.setId_funcao(imp.getId_funcao());
        vo.setId_tipoNivelOperador(imp.getId_tipoNivelOperador());
        return vo;
        
    }
    
    public void importarFuncaoOperador(List<PdvFuncaoOperadorIMP> funcao) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Funções tabela existentes...", funcao.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (PdvFuncaoOperadorIMP imp : funcao) {
                PdvFuncaoOperadorVO vo = new PdvFuncaoOperadorVO();
                vo = converter(imp);
                provider.gravar(vo);
            }
            
            LOG.finest("");
            
            provider.setStatus();
            
            provider.commit();
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }
}
