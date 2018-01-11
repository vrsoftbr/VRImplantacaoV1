package vrimplantacao2.dao.cadastro.financeiro;

import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContasPagarProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContasPagarRepository;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoProvider;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoRepository;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.ReceberCaixaRepository;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.ReceberCaixaRepositoryProvider;

/**
 * Repositório do financeiro.
 * @author Leandro
 */
public class FinanceiroRepository {
    private ContasPagarRepository contasPagarRepository;
    private ReceberCaixaRepository recebimentoCaixaRepository;
    private CreditoRotativoRepository creditoRotativoRepository;

    public FinanceiroRepository(String sistema, String agrupador, int lojaVR) throws Exception {        
        this.contasPagarRepository = new ContasPagarRepository(
                new ContasPagarProvider(
                        sistema, 
                        agrupador,
                        lojaVR
                )
        );
        this.recebimentoCaixaRepository = new ReceberCaixaRepository(
                new ReceberCaixaRepositoryProvider(
                        sistema, 
                        agrupador,
                        lojaVR
                )
        );
        this.creditoRotativoRepository = new CreditoRotativoRepository (
                new CreditoRotativoProvider(
                        sistema, 
                        agrupador,
                        lojaVR
                )
        );
    }

    public ContasPagarRepository getContasPagar() {
        return this.contasPagarRepository;
    }
    
    public ReceberCaixaRepository getRecebimentoCaixa() {
        return this.recebimentoCaixaRepository;
    }

    public CreditoRotativoRepository getCreditoRotativo() {
        return creditoRotativoRepository;
    }
    
    
}
