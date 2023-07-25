package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.EntradaSaidaTipoEntradaIMP;
import vrimplantacao2_5.Financeiro.Provider.EntradaSaidaTipoEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.EntradaSaidaTipoEntradaVO;

public class EntradaSaidaTipoEntradaRepository {
    
    private static final Logger LOG = Logger.getLogger(EntradaSaidaTipoEntradaRepository.class.getName());
    
    private final EntradaSaidaTipoEntradaRepositoryProvider provider;
    
    public EntradaSaidaTipoEntradaRepository(EntradaSaidaTipoEntradaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public EntradaSaidaTipoEntradaVO converter(EntradaSaidaTipoEntradaIMP imp) {
        EntradaSaidaTipoEntradaVO vo = new EntradaSaidaTipoEntradaVO();
        
        vo.setId(imp.getId());
        vo.setId_tipoEntrada(imp.getId_tipoEntrada());
        
        return vo;
        
    }
    
    public void importarEntradaSaidaTipoEntrada(List<EntradaSaidaTipoEntradaIMP> entradaSaidaTipoEntrada) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Entrada Saida Tipo Entrada existentes...", entradaSaidaTipoEntrada.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (EntradaSaidaTipoEntradaIMP imp : entradaSaidaTipoEntrada) {
                EntradaSaidaTipoEntradaVO vo = new EntradaSaidaTipoEntradaVO();
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
