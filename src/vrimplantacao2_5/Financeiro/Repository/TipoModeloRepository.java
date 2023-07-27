package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.TipoModeloIMP;
import vrimplantacao2_5.Financeiro.Provider.TipoModeloRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.TipoModeloVO;

public class TipoModeloRepository {
    
    private static final Logger LOG = Logger.getLogger(TipoModeloRepository.class.getName());
    
    private final TipoModeloRepositoryProvider provider;
    
    public TipoModeloRepository(TipoModeloRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public TipoModeloVO converter(TipoModeloIMP imp) {
        TipoModeloVO vo = new TipoModeloVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoMarca(imp.getId_tipoMarca());
        vo.setDescricao(imp.getDescricao());
        vo.setCodigoM(imp.getCodigoM());
        vo.setSat(imp.isSat());
        return vo;
        
    }
    
    public void importarModelo(List<TipoModeloIMP> modelo) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Modelo existentes...", modelo.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (TipoModeloIMP imp : modelo) {
                TipoModeloVO vo = new TipoModeloVO();
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
