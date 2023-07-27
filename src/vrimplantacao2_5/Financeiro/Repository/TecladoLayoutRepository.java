package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.AtivoImobilizadoIMP;
import vrimplantacao2_5.Financeiro.IMP.TecladoLayoutIMP;
import vrimplantacao2_5.Financeiro.Provider.TecladoLayoutRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.AtivoImobilizadoVO;
import vrimplantacao2_5.Financeiro.VO.TecladoLayoutVO;

public class TecladoLayoutRepository {
    
    private static final Logger LOG = Logger.getLogger(TecladoLayoutRepository.class.getName());
    
    private final TecladoLayoutRepositoryProvider provider;
    
    public TecladoLayoutRepository(TecladoLayoutRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public TecladoLayoutVO converter(TecladoLayoutIMP imp) {
        TecladoLayoutVO vo = new TecladoLayoutVO();
        
        vo.setId(imp.getId());
        vo.setId_loja(imp.getId_loja());
        vo.setDescricao(imp.getDescricao());
        
        return vo;
        
    }
    
    public void importarTecladoLayout(List<TecladoLayoutIMP> teclado) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Teclado Layout existentes...", teclado.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (TecladoLayoutIMP imp : teclado) {
                TecladoLayoutVO vo = new TecladoLayoutVO();
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
