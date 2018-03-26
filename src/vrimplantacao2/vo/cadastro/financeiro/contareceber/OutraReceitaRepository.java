package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ContaReceberPagamentoIMP;

/**
 *
 * @author Leandro
 */
public class OutraReceitaRepository {

    private static final Logger LOG = Logger.getLogger(OutraReceitaRepository.class.getName());
    
    private final OutraReceitaRepositoryProvider provider;

    public OutraReceitaRepository(OutraReceitaRepositoryProvider provider) {
        this.provider = provider;
    }

    public void importar(List<ContaReceberIMP> contas, Set<OpcaoContaReceber> opt) throws Exception {
        
        provider.setStatus("Contas a Receber...Carregando dados");        
        
        Map<String, Integer> fornecedores = provider.getFornecedores();
        LOG.fine("Fornecedores carregados: " + fornecedores.size());
        Map<String, ContaReceberAnteriorVO> anteriores = provider.getAnteriores();
        LOG.fine("Anteriores carregados: " + anteriores.size());
        
        provider.setStatus("Contas a Receber...Gravando...", contas.size());
        
        try {
            provider.begin();
            
            for (ContaReceberIMP imp: contas) {
                
                ContaReceberAnteriorVO anterior = anteriores.get(imp.getId());
                
                if (anterior == null) {
                    anterior = converterAnterior(imp);                    
                    OutraReceitaVO vo = converterOutraReceita(imp);                    
                    provider.gravar(vo);                    
                    anterior.setCodigoAtual(vo.getId());
                    provider.gravar(anterior);
                }
                
                if (anterior.getCodigoAtual() > 0) {
                    for (ContaReceberPagamentoIMP impPag: imp.getPagamentos()) {
                        
                    }
                }
                
                provider.setStatus();
            }
            
            provider.commit();            
        } catch (Exception ex) {
            provider.rollback();
            throw ex;
        }
    }

    public ContaReceberAnteriorVO converterAnterior(ContaReceberIMP imp) {
        ContaReceberAnteriorVO vo = new ContaReceberAnteriorVO();
        
        vo.setSistema(provider.getSistema());
        vo.setLoja(provider.getLoja());
        vo.setId(imp.getId());
        vo.setData(imp.getDataEmissao());
        vo.setIdFornecedor(imp.getIdFornecedor());
        vo.setIdClienteEventual(imp.getIdClienteEventual());
        vo.setVencimento(imp.getDataVencimento());
        vo.setValor(imp.getValor());
        
        return vo;
    }

    private OutraReceitaVO converterOutraReceita(ContaReceberIMP imp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
