package vrimplantacao2_5.tipoRecebivel.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.tipoRecebivel.IMP.TipoSaidaNotaFiscalSequenciaIMP;
import vrimplantacao2_5.tipoRecebivel.Provider.TipoSaidaNotaSaidaSequenciaRepositoryProvider;
import vrimplantacao2_5.tipoRecebivel.VO.TipoSaidaNotaFiscalSequenciaVO;

public class TipoSaidaNotaSaidaSequenciaRepository {
    
    private static final Logger LOG = Logger.getLogger(TipoSaidaNotaSaidaSequenciaRepository.class.getName());
    
    private final TipoSaidaNotaSaidaSequenciaRepositoryProvider provider;
    
    public TipoSaidaNotaSaidaSequenciaRepository(TipoSaidaNotaSaidaSequenciaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public TipoSaidaNotaFiscalSequenciaVO converter(TipoSaidaNotaFiscalSequenciaIMP imp) {
        TipoSaidaNotaFiscalSequenciaVO vo = new TipoSaidaNotaFiscalSequenciaVO();
        
        vo.setId(imp.getId());
        vo.setId_loja(imp.getId_loja());
        vo.setId_tipoSaida(imp.getId_tipoSaida());
        
        return vo;
        
    }
    
    public void importarSequenceTipoSaida(List<TipoSaidaNotaFiscalSequenciaIMP> tipoSaidaSeqSaida) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Sequencia Tipo Saida existentes...", tipoSaidaSeqSaida.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (TipoSaidaNotaFiscalSequenciaIMP imp : tipoSaidaSeqSaida) {
                TipoSaidaNotaFiscalSequenciaVO vo = new TipoSaidaNotaFiscalSequenciaVO();
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
