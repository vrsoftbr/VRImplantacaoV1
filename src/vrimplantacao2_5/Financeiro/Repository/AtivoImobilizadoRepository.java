package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.AtivoImobilizadoIMP;
import vrimplantacao2_5.Financeiro.Provider.AtivoImobilizadoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.AtivoImobilizadoVO;

public class AtivoImobilizadoRepository {
    
    private static final Logger LOG = Logger.getLogger(AtivoImobilizadoRepository.class.getName());
    
    private final AtivoImobilizadoRepositoryProvider provider;
    
    public AtivoImobilizadoRepository(AtivoImobilizadoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public AtivoImobilizadoVO converter(AtivoImobilizadoIMP imp) {
        AtivoImobilizadoVO vo = new AtivoImobilizadoVO();
        
        vo.setId(imp.getId());
        vo.setId_contaContabilFiscal(imp.getId_contaContabilFiscal());
        vo.setId_HistoricoPadrao(imp.getId_HistoricoPadrao());
        
        return vo;
        
    }
    
    public void importarAtivo(List<AtivoImobilizadoIMP> ativo) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Ativo Imobilizado existentes...", ativo.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (AtivoImobilizadoIMP imp : ativo) {
                AtivoImobilizadoVO vo = new AtivoImobilizadoVO();
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
