package vrimplantacao2_5.Financeiro.Repository;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2_5.Financeiro.IMP.RecebivelConfiguracaoTabelaIMP;
import vrimplantacao2_5.Financeiro.Provider.RecebivelConfiguracaoTabelaRepositoryProvider;
import vrimplantacao2_5.Financeiro.VO.RecebivelConfiguracaoTabelaVO;

public class RecebivelConfiguracaoTabelaRepository {
    
    private static final Logger LOG = Logger.getLogger(RecebivelConfiguracaoTabelaRepository.class.getName());
    
    private final RecebivelConfiguracaoTabelaRepositoryProvider provider;
    
    public RecebivelConfiguracaoTabelaRepository(RecebivelConfiguracaoTabelaRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public RecebivelConfiguracaoTabelaVO converter(RecebivelConfiguracaoTabelaIMP imp) {
        RecebivelConfiguracaoTabelaVO vo = new RecebivelConfiguracaoTabelaVO();
        
        vo.setId(imp.getId());
        vo.setId_recebivelConfiguracao(imp.getId_recebivelConfiguracao());
        vo.setQuantidadeDeDia(imp.getQuantidadeDeDia());
        vo.setUtilizaRegra(imp.isUtilizaRegra());
        vo.setUtilizaDataCorte(imp.isUtilizaDataCorte());
        vo.setId_tipoVencimentoRecebivel(imp.getId_tipoVencimentoRecebivel());
        vo.setDiasUteis(imp.isDiasUteis());
        vo.setProximoDiaUtil(imp.isProximoDiaUtil());
        
        return vo;
        
    }
    
    public void importarConfiguracaoRecebivelTabela(List<RecebivelConfiguracaoTabelaIMP> confRecebivelTabela) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Configuracao de Recebiveis tabela existentes...", confRecebivelTabela.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (RecebivelConfiguracaoTabelaIMP imp : confRecebivelTabela) {
                RecebivelConfiguracaoTabelaVO vo = new RecebivelConfiguracaoTabelaVO();
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
