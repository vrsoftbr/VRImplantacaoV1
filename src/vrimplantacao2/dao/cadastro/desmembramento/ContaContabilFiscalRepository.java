package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContaContabilFiscalRepositoryProvider;
import vrimplantacao2.vo.cadastro.ContaContabilFiscaVO;
import vrimplantacao2.vo.importacao.ContaContabilFiscalIMP;

public class ContaContabilFiscalRepository {
    
    private static final Logger LOG = Logger.getLogger(ContaContabilFiscalRepository.class.getName());
    
    private final ContaContabilFiscalRepositoryProvider provider;
    
    public ContaContabilFiscalRepository(ContaContabilFiscalRepositoryProvider provider) {
        this.provider = provider;
    }
    
    public ContaContabilFiscaVO converter(ContaContabilFiscalIMP imp) {
        ContaContabilFiscaVO vo = new ContaContabilFiscaVO();
        
        vo.setId(imp.getId());
        vo.setDescricao(imp.getDescricao());
        vo.setConta1(imp.getConta1());
        vo.setConta2(imp.getConta2());
        vo.setConta3(imp.getConta3());
        vo.setConta4(imp.getConta4());
        vo.setConta5(imp.getConta5());
        vo.setNivel(imp.getNivel());
        vo.setId_situacaoCadastro(imp.getId_situacaoCadastro());
        vo.setContaReduzida(imp.getContaReduzida());
        vo.setResultado(imp.isResultado());
        vo.setData(imp.getData());
        vo.setDmpl(imp.isDmpl());
        vo.setContaCompensacao(imp.isContaCompensacao());
        vo.setNotaExplicativa(imp.getNotaExplicativa());
        
        return vo;
        
    }
    
    public void importarContaContabilFiscal(List<ContaContabilFiscalIMP> contaContabil) throws Exception {
        
        provider.begin();
        try {
            provider.setStatus("Carregando Tipo Tef existentes...", contaContabil.size());
            LOG.info("Iniciando gravação dos recebiveís");
            
            for (ContaContabilFiscalIMP imp : contaContabil) {
                ContaContabilFiscaVO vo = new ContaContabilFiscaVO();
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
