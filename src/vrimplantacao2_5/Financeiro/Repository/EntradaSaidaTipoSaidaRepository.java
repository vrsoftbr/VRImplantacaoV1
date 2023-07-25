package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.EntradaSaidaTipoSaidaIMP;
import vrimplantacao2_5.Financeiro.Provider.EntradaSaidaTipoSaidaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.EntradaSaidaTipoSaidaVO;

public class EntradaSaidaTipoSaidaRepository {
    
    private static final Logger LOG = Logger.getLogger(EntradaSaidaTipoSaidaRepository.class.getName());
    
    private final EntradaSaidaTipoSaidaRepositoryProvider provider;
    
    public EntradaSaidaTipoSaidaRepository(EntradaSaidaTipoSaidaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public EntradaSaidaTipoSaidaVO converter(EntradaSaidaTipoSaidaIMP imp) {
        EntradaSaidaTipoSaidaVO vo = new EntradaSaidaTipoSaidaVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoSaida(imp.getId_tipoSaida());
        
        return vo;
        
    }
    
    public void importarEntradaSaidaTipoSaida(List<EntradaSaidaTipoSaidaIMP> entradaSaida) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Entrada Saida existentes...", entradaSaida.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (EntradaSaidaTipoSaidaIMP imp : entradaSaida) {
                EntradaSaidaTipoSaidaVO vo = new EntradaSaidaTipoSaidaVO();
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
