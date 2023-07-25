package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.TipoPlanoContaIMP;
import vrimplantacao2_5.Financeiro.Provider.TipoPlanoContaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.TipoPlanoContaVO;

public class TipoPlanoContaRepository {
    
    private static final Logger LOG = Logger.getLogger(TipoPlanoContaRepository.class.getName());
    
    private final TipoPlanoContaRepositoryProvider provider;
    
    public TipoPlanoContaRepository(TipoPlanoContaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public TipoPlanoContaVO converter(TipoPlanoContaIMP imp) {
        TipoPlanoContaVO vo = new TipoPlanoContaVO();
        
        vo.setId(imp.getId());
        vo.setPlanoConta1(imp.getPlanoConta1());
        vo.setPlanoConta2(imp.getPlanoConta2());
        vo.setNivel(imp.getNivel());
        vo.setDescricao(imp.getDescricao());
        
        return vo;
        
    }
    
    public void importarTipoPlano(List<TipoPlanoContaIMP> tipoPlano) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Plano Conta existentes...", tipoPlano.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (TipoPlanoContaIMP imp : tipoPlano) {
                TipoPlanoContaVO vo = new TipoPlanoContaVO();
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
