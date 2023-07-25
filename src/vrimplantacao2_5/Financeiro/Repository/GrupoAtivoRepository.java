package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.GrupoAtivoIMP;
import vrimplantacao2_5.Financeiro.Provider.GrupoAtivoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.GrupoAtivoVO;

public class GrupoAtivoRepository {
    
    private static final Logger LOG = Logger.getLogger(GrupoAtivoRepository.class.getName());
    
    private final GrupoAtivoRepositoryProvider provider;
    
    public GrupoAtivoRepository(GrupoAtivoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public GrupoAtivoVO converter(GrupoAtivoIMP imp) {
        GrupoAtivoVO vo = new GrupoAtivoVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setId_contaContabilAtivo(imp.getId_contaContabilAtivo());
        vo.setId_contaContabilDepreciacao(imp.getId_contaContabilDepreciacao());
        vo.setId_contaCOntabilDespesaDepreciacao(imp.getId_contaCOntabilDespesaDepreciacao());
        vo.setId_contaContabilCustoDepreciacao(imp.getId_contaCOntabilDespesaDepreciacao());
        
        return vo;
        
    }
    
    public void importarGrupoAtivo(List<GrupoAtivoIMP> grupoAtivo) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Grupo Ativo existentes...", grupoAtivo.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (GrupoAtivoIMP imp : grupoAtivo) {
                GrupoAtivoVO vo = new GrupoAtivoVO();
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
