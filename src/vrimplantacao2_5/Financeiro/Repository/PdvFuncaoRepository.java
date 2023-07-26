package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.CfopEntradaIMP;
import vrimplantacao2_5.Financeiro.IMP.PdvFuncaoIMP;
import vrimplantacao2_5.Financeiro.Provider.PdvFuncaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.CfopEntradaVO;
import vrimplantacao2_5.Financeiro.VO.PdvFuncaoVO;

public class PdvFuncaoRepository {
    
    private static final Logger LOG = Logger.getLogger(PdvFuncaoRepository.class.getName());
    
    private final PdvFuncaoRepositoryProvider provider;
    
    public PdvFuncaoRepository(PdvFuncaoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public PdvFuncaoVO converter(PdvFuncaoIMP imp) {
        PdvFuncaoVO vo = new PdvFuncaoVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setFechado(imp.isFechado());
        vo.setParcial(imp.isParcial());
        vo.setDisponivel(imp.isDisponivel());
        vo.setVenda(imp.isVenda());
        vo.setPagamento(imp.isPagamento());
        vo.setPausa(imp.isPausa());
        vo.setSelfCheckout(imp.isSelfCheckout());
        vo.setBalanco(imp.isBalanco());
        
        return vo;
        
    }
    
    public void importarPdvFuncao(List<PdvFuncaoIMP> funcao) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando CFOP Entrada existentes...", funcao.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (PdvFuncaoIMP imp : funcao) {
                PdvFuncaoVO vo = new PdvFuncaoVO();
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
