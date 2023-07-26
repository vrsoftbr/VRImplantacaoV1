package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeTipoEntradaIMP;
import vrimplantacao2_5.Financeiro.IMP.PdvTecladoFuncaoIMP;
import vrimplantacao2_5.Financeiro.Provider.PdvTecladoFuncaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.ContabilidadeTipoEntradaVO;
import vrimplantacao2_5.Financeiro.VO.PdvTecladoFuncaoVO;

public class PdvTecladoFuncaoRepository {
    
    private static final Logger LOG = Logger.getLogger(PdvTecladoFuncaoRepository.class.getName());
    
    private final PdvTecladoFuncaoRepositoryProvider provider;
    
    public PdvTecladoFuncaoRepository(PdvTecladoFuncaoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public PdvTecladoFuncaoVO converter(PdvTecladoFuncaoIMP imp) {
        PdvTecladoFuncaoVO vo = new PdvTecladoFuncaoVO();
        
        vo.setId(imp.getId());
        vo.setId_tecladoLayout(imp.getId_tecladoLayout());
        vo.setCodigoRetorno(imp.getCodigoRetorno());
        vo.setId_funcao(imp.getId_funcao());
        
        return vo;
        
    }
    
    public void importarFuncaoeTeclado(List<PdvTecladoFuncaoIMP> teclado) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Funcao Teclado existentes...", teclado.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (PdvTecladoFuncaoIMP imp : teclado) {
                PdvTecladoFuncaoVO vo = new PdvTecladoFuncaoVO();
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
