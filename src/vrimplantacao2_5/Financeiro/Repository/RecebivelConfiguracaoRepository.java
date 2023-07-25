package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.RecebivelConfiguracaoIMP;
import vrimplantacao2_5.Financeiro.Provider.RecebivelConfiguracaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.RecebivelConfiguracaoVO;

public class RecebivelConfiguracaoRepository {
    
    private static final Logger LOG = Logger.getLogger(RecebivelConfiguracaoRepository.class.getName());
    
    private final RecebivelConfiguracaoRepositoryProvider provider;
    
    public RecebivelConfiguracaoRepository(RecebivelConfiguracaoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public RecebivelConfiguracaoVO converter(RecebivelConfiguracaoIMP imp) {
        RecebivelConfiguracaoVO vo = new RecebivelConfiguracaoVO();
        
        vo.setId(imp.getId());
        vo.setId_loja(imp.getId_loja());
        vo.setId_banco(imp.getId_banco());
        vo.setId_tipoRecebivel(imp.getId_tipoRecebivel());
        vo.setId_tipoVencimentoRecebivel(imp.getId_tipoVencimentoRecebivel());
        vo.setTaxa(imp.getTaxa());
        vo.setUtilizaRegra(imp.isUtilizaRegra());
        vo.setUtilizaTabela(imp.isUtilizaTabela());
        vo.setUtilizaDataCorte(imp.isUtilizaDataCorte());
        vo.setAgencia(imp.getAgencia());
        vo.setConta(imp.getAgencia());
        vo.setQuantidadeDiaFixo(imp.getQuantidadeDiaFixo());
        vo.setDiaSemanaCorte(imp.getDiaSemanaCorte());
        vo.setPeriodoCorte(imp.getPeriodoCorte());
        vo.setDataInicioCorte(imp.getDataInicioCorte());
        vo.setOutrasTaxas(imp.getOutrasTaxas());
        vo.setDiasUteis(imp.isDiasUteis());
        vo.setProximoDiaUtil(imp.isProximoDiaUtil());
        
        return vo;
        
    }
    
    public void importarConfiguracaoRecebivel(List<RecebivelConfiguracaoIMP> config) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Configuração de Recebível existentes...", config.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (RecebivelConfiguracaoIMP imp : config) {
                RecebivelConfiguracaoVO vo = new RecebivelConfiguracaoVO();
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
