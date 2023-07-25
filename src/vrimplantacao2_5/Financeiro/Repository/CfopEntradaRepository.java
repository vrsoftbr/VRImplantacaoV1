package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.CfopEntradaIMP;
import vrimplantacao2_5.Financeiro.Provider.CfopEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.CfopEntradaVO;

public class CfopEntradaRepository {
    
    private static final Logger LOG = Logger.getLogger(CfopEntradaRepository.class.getName());
    
    private final CfopEntradaRepositoryProvider provider;
    
    public CfopEntradaRepository(CfopEntradaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public CfopEntradaVO converter(CfopEntradaIMP imp) {
        CfopEntradaVO vo = new CfopEntradaVO();
        
        vo.setId(imp.getId());
        vo.setCfop(imp.getCfop());
        vo.setId_tipoEntrada(imp.getId_tipoEntrada());
        
        return vo;
        
    }
    
    public void importarCfopEntrada(List<CfopEntradaIMP> cfopEntrada) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando CFOP Entrada existentes...", cfopEntrada.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (CfopEntradaIMP imp : cfopEntrada) {
                CfopEntradaVO vo = new CfopEntradaVO();
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
