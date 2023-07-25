package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.CfopIMP;
import vrimplantacao2_5.Financeiro.Provider.CfopRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.CfopVO;

public class CfopRepository {
    
    private static final Logger LOG = Logger.getLogger(CfopRepository.class.getName());
    
    private final CfopRepositoryProvider provider;
    
    public CfopRepository(CfopRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public CfopVO converter(CfopIMP imp) {
        CfopVO vo = new CfopVO();
        
        vo.setId(imp.getId());
        vo.setCfop(imp.getCfop());
        vo.setDescricao(imp.getDescricao());
        vo.setForaEstado(imp.isForaEstado());
        vo.setSubstituido(imp.isSubstituido());
        vo.setTipoEntradaSaida(imp.getTipoEntradaSaida());
        vo.setGeraIcms(imp.isGeraIcms());
        vo.setBonificado(imp.isBonificacao());
        vo.setDevolucao(imp.isDevolucao());
        vo.setVendaEcf(imp.isVendaEcf());
        vo.setDevolucaoCliente(imp.isDevolucaoCliente());
        vo.setServico(imp.isServico());
        vo.setFabricacaoPropria(imp.isFabricacaoPropria());
        vo.setExportacao(imp.isExportacao());
        
        return vo;
        
    }
    
    public void importarCfop(List<CfopIMP> caixa) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando CFOP existentes...", caixa.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (CfopIMP imp : caixa) {
                CfopVO vo = new CfopVO();
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
