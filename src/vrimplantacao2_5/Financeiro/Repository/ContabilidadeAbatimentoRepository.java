package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeAbatimentoIMP;
import vrimplantacao2_5.Financeiro.Provider.ContabilidadeAbatimentoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.ContabilidadeAbatimentoVO;

public class ContabilidadeAbatimentoRepository {
    
    private static final Logger LOG = Logger.getLogger(ContabilidadeAbatimentoRepository.class.getName());
    
    private final ContabilidadeAbatimentoRepositoryProvider provider;
    
    public ContabilidadeAbatimentoRepository(ContabilidadeAbatimentoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public ContabilidadeAbatimentoVO converter(ContabilidadeAbatimentoIMP imp) {
        ContabilidadeAbatimentoVO vo = new ContabilidadeAbatimentoVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoAbatimento(imp.getId_tipoAbatimento());
        vo.setId_contaContabilFiscal(imp.getId_contaContabilFiscal());
        vo.setId_HistoricoPadrao(imp.getId_HistoricoPadrao());
        vo.setId_centroCusto(imp.getId_centroCusto());
        
        return vo;
        
    }
    
    public void importarAbatimento(List<ContabilidadeAbatimentoIMP> abatimento) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Abatimento existentes...", abatimento.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (ContabilidadeAbatimentoIMP imp : abatimento) {
                ContabilidadeAbatimentoVO vo = new ContabilidadeAbatimentoVO();
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
