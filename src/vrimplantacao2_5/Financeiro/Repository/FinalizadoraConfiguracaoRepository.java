package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.AtivoImobilizadoIMP;
import vrimplantacao2_5.Financeiro.IMP.FinalizadoraConfiguracaoIMP;
import vrimplantacao2_5.Financeiro.Provider.FinalizadoraConfiguracaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.AtivoImobilizadoVO;
import vrimplantacao2_5.Financeiro.VO.FinalizadoraConfiguracaoVO;

public class FinalizadoraConfiguracaoRepository {
    
    private static final Logger LOG = Logger.getLogger(FinalizadoraConfiguracaoRepository.class.getName());
    
    private final FinalizadoraConfiguracaoRepositoryProvider provider;
    
    public FinalizadoraConfiguracaoRepository(FinalizadoraConfiguracaoRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public FinalizadoraConfiguracaoVO converter(FinalizadoraConfiguracaoIMP imp) {
        FinalizadoraConfiguracaoVO vo = new FinalizadoraConfiguracaoVO();
        
        vo.setId(imp.getId());
        vo.setId_loja(imp.getId_loja());
        vo.setId_finalizadora(imp.getId_finalizadora());
        vo.setAceitaTroco(imp.isAceitaTroco());
        vo.setAceitaRetirada(imp.isAceitaRetiradad());
        vo.setAceitaAbastecimento(imp.isAceitaAbastecimento());
        vo.setAceitaRecebimento(imp.isAceitaRecebimento());
        vo.setUtilizaContraVale(imp.isUtilizaContraVale());
        vo.setRetiradaTotal(imp.isRetiradaTotal());
        vo.setValorMaximoTroco(imp.getValorMaximoTroco());
        vo.setJuros(imp.getJuros());
        vo.setTipoMaximoTroco(imp.getTipoMaximoTroco());
        vo.setAceitaRetiradaCf(imp.isAceitaRetiradaCf());
        vo.setUtilizado(imp.isUtilizado());
        vo.setAvisaRetirada(imp.isAvisaRetirada());
        
        return vo;
        
    }
    
    public void finalizadoraConf(List<FinalizadoraConfiguracaoIMP> finalizadora) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Configuracao de Finalizadora existentes...", finalizadora.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (FinalizadoraConfiguracaoIMP imp : finalizadora) {
                FinalizadoraConfiguracaoVO vo = new FinalizadoraConfiguracaoVO();
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
