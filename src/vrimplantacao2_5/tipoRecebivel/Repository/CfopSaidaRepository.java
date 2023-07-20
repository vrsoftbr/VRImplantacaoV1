package vrimplantacao2_5.tipoRecebivel.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.tipoRecebivel.IMP.CfopSaidaIMP;
import vrimplantacao2_5.tipoRecebivel.Provider.CfopSaidaRepositoryProvider;
import vrimplantacao2_5.tipoRecebivel.VO.CfopSaidaVO;

public class CfopSaidaRepository {
    
    private static final Logger LOG = Logger.getLogger(CfopSaidaRepository.class.getName());
    
    private final CfopSaidaRepositoryProvider provider;
    
    public CfopSaidaRepository(CfopSaidaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public CfopSaidaVO converter(CfopSaidaIMP imp) {
        CfopSaidaVO vo = new CfopSaidaVO();
        
        vo.setId(imp.getId());
        vo.setCfop(imp.getCfop());
        vo.setId_tipoSaida(imp.getId_tipoSaida());
        
        return vo;
        
    }
    
    public void importarCfopSaida(List<CfopSaidaIMP> cfopSaida) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando CFOP Saida existentes...", cfopSaida.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (CfopSaidaIMP imp : cfopSaida) {
                CfopSaidaVO vo = new CfopSaidaVO();
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
