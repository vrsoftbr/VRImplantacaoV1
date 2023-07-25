package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeTipoEntradaIMP;
import vrimplantacao2_5.Financeiro.Provider.ContabilidadeTipoEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.ContabilidadeTipoEntradaVO;

public class ContabilidadeTipoEntradaRepository {
    
    private static final Logger LOG = Logger.getLogger(ContabilidadeTipoEntradaRepository.class.getName());
    
    private final ContabilidadeTipoEntradaRepositoryProvider provider;
    
    public ContabilidadeTipoEntradaRepository(ContabilidadeTipoEntradaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public ContabilidadeTipoEntradaVO converter(ContabilidadeTipoEntradaIMP imp) {
        ContabilidadeTipoEntradaVO vo = new ContabilidadeTipoEntradaVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoValor(imp.getId_tipoValor());
        vo.setId_contaContaContabilDebito(imp.getId_contaContaContabilDebito());
        vo.setId_contaContabilCredito(imp.getId_contaContabilCredito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        
        return vo;
        
    }
    
    public void importarTipoEntradaContabil(List<ContabilidadeTipoEntradaIMP> TipoEntradaContabil) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Entrada Contabil existentes...", TipoEntradaContabil.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (ContabilidadeTipoEntradaIMP imp : TipoEntradaContabil) {
                ContabilidadeTipoEntradaVO vo = new ContabilidadeTipoEntradaVO();
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
