package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeTipoSaidaIMP;
import vrimplantacao2_5.Financeiro.Provider.ContabilidadeTipoSaidaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.ContabilidadeTipoSaidaVO;

public class ContabilidadeTipoSaidaRepository {
    
    private static final Logger LOG = Logger.getLogger(ContabilidadeTipoSaidaRepository.class.getName());
    
    private final ContabilidadeTipoSaidaRepositoryProvider provider;
    
    public ContabilidadeTipoSaidaRepository(ContabilidadeTipoSaidaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public ContabilidadeTipoSaidaVO converter(ContabilidadeTipoSaidaIMP imp) {
        ContabilidadeTipoSaidaVO vo = new ContabilidadeTipoSaidaVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoValor(imp.getId_tipoValor());
        vo.setId_contaContaContabilDebito(imp.getId_contaContaContabilDebito());
        vo.setId_contaContabilCredito(imp.getId_contaContabilCredito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        
        return vo;
        
    }
    
    public void importarTipoEntradaContabil(List<ContabilidadeTipoSaidaIMP> TipoSaidaContabil) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Saida Contabil existentes...", TipoSaidaContabil.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (ContabilidadeTipoSaidaIMP imp : TipoSaidaContabil) {
                ContabilidadeTipoSaidaVO vo = new ContabilidadeTipoSaidaVO();
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
