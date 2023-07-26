package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.FinalizadoraIMP;
import vrimplantacao2_5.Financeiro.Provider.FinalizadoraRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.FinalizadoraVO;

public class FinalizadoraRepository {
    
    private static final Logger LOG = Logger.getLogger(FinalizadoraRepository.class.getName());
    
    private final FinalizadoraRepositoryProvider provider;
    
    public FinalizadoraRepository(FinalizadoraRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public FinalizadoraVO converter(FinalizadoraIMP imp) {
        FinalizadoraVO vo = new FinalizadoraVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setId_funcao(imp.getId_funcao());
        vo.setConsultaCheque(imp.isConsultaCheque());
        vo.setConsultaCartao(imp.isConsultaCartao());
        vo.setConsultaTef(imp.isConsultaTef());
        vo.setConsultaTicket(imp.isConsultaTicket());
        vo.setConsultaConvenio(imp.isConsultaConvenio());
        vo.setVerificaPlano(imp.isVerificaPlano());
        vo.setConsultaCreditoRotativo(imp.isConsultaCreditoRotativo());
        vo.setConsultaNotaFiscal(imp.isConsultaNotaFiscal());
        
        return vo;
        
    }
    
    public void importarFinalizadora(List<FinalizadoraIMP> finalizadora) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Finalizadoras existentes...", finalizadora.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (FinalizadoraIMP imp : finalizadora) {
                FinalizadoraVO vo = new FinalizadoraVO();
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
