package vrimplantacao2.dao.cadastro.financeiro;

import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContasPagarProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContasPagarRepository;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoProvider;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoRepository;
import vrimplantacao2.dao.cadastro.financeiro.devolucao.DevolucaoProvider;
import vrimplantacao2.dao.cadastro.financeiro.devolucao.DevolucaoRepository;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.ReceberCaixaRepository;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.ReceberCaixaRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.verbas.VerbasProvider;
import vrimplantacao2.dao.cadastro.financeiro.verbas.VerbasRepository;

/**
 * Repositório do financeiro.
 * @author Leandro
 */
public class FinanceiroRepository {
    private ContasPagarRepository contasPagarRepository;
    private ReceberCaixaRepository recebimentoCaixaRepository;
    private CreditoRotativoRepository creditoRotativoRepository;
    private VerbasRepository verbasRepository;
    private DevolucaoRepository devolucaoRepository;

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
        this.verbasRepository = new VerbasRepository (
                new VerbasProvider(
                        sistema, 
                        agrupador,
                        lojaVR
                )
        );
        this.devolucaoRepository = new DevolucaoRepository (
                new DevolucaoProvider(
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
    
    public VerbasRepository getVerbas() {
        return this.verbasRepository;
    }
        
    public DevolucaoRepository getDevolucao() {
        return this.devolucaoRepository;
    }
}
