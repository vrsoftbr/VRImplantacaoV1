package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContaContabilFinanceirolRepositoryProvider;
import vrimplantacao2.vo.cadastro.ContaContabilFinanceiroVO;
import vrimplantacao2.vo.importacao.ContaContabilFinanceiroIMP;

public class ContaContabilFinanceiroRepository {
    
    private static final Logger LOG = Logger.getLogger(ContaContabilFinanceiroRepository.class.getName());
    
    private final ContaContabilFinanceirolRepositoryProvider provider;
    
    public ContaContabilFinanceiroRepository(ContaContabilFinanceirolRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public ContaContabilFinanceiroVO converter(ContaContabilFinanceiroIMP imp) {
        ContaContabilFinanceiroVO vo = new ContaContabilFinanceiroVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setId_situacaoCadastro(imp.getId_situacaoCadastro());
        vo.setId_contaContabilFiscal(imp.getId_contaContabilFiscal());
        vo.setTransferencia(imp.isTransferencia());
        vo.setId_historicoPadrao(imp.getId_historicoPadrao());
        vo.setContabiliza(imp.isContabiliza());
        vo.setId_tipoCentroCusto(imp.getId_tipoCentroCusto());
        
        return vo;
        
    }
    
    public void importarContaContabilFiscal(List<ContaContabilFinanceiroIMP> contaContabil) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Conta Contabil Financeiro existentes...", contaContabil.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (ContaContabilFinanceiroIMP imp : contaContabil) {
                ContaContabilFinanceiroVO vo = new ContaContabilFinanceiroVO();
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
