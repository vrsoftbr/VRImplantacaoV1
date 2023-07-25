package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.CaixaDiferencaIMP;
import vrimplantacao2_5.Financeiro.Provider.CaixaDiferencaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.CaixaDiferencaVO;

public class CaixaDiferencaRepository {
    
    private static final Logger LOG = Logger.getLogger(CaixaDiferencaRepository.class.getName());
    
    private final CaixaDiferencaRepositoryProvider provider;
    
    public CaixaDiferencaRepository(CaixaDiferencaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public CaixaDiferencaVO converter(CaixaDiferencaIMP imp) {
        CaixaDiferencaVO vo = new CaixaDiferencaVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoValor(imp.getId_tipoValor());
        vo.setId_contaContaContabilFiscalDebito(imp.getId_contaContaContabilFiscalDebito());
        vo.setId_contaContabilFiscalCredito(imp.getId_contaContabilFiscalCredito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        vo.setId_centroCusto(imp.getId_centroCusto());
        
        return vo;
        
    }
    
    public void importarCaixaDiferenca(List<CaixaDiferencaIMP> caixa) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Caixa Diferenca existentes...", caixa.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (CaixaDiferencaIMP imp : caixa) {
                CaixaDiferencaVO vo = new CaixaDiferencaVO();
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
