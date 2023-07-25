package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.MapaResumoIMP;
import vrimplantacao2_5.Financeiro.Provider.MapaResumoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.MapaResumoVO;

public class MapaResumoRepository {
    
    private static final Logger LOG = Logger.getLogger(MapaResumoRepository.class.getName());
    
    private final MapaResumoRepositoryProvider provider;
    
    public MapaResumoRepository(MapaResumoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public MapaResumoVO converter(MapaResumoIMP imp) {
        MapaResumoVO vo = new MapaResumoVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoValor(imp.getId_tipoValor());
        vo.setId_contaContabilFiscalDebito(imp.getId_contaContabilFiscalDebito());
        vo.setId_contaContabilFiscalCredito(imp.getId_contaContabilFiscalCredito());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        
        return vo;
        
    }
    
    public void importarCaixaDiferenca(List<MapaResumoIMP> mapa) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Mapa Resumo existentes...", mapa.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (MapaResumoIMP imp : mapa) {
                MapaResumoVO vo = new MapaResumoVO();
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
