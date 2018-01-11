package vrimplantacao2.dao.cadastro.financeiro;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContasPagarRepositoryTest;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.ReceberCaixaRepositoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ContasPagarRepositoryTest.class,
    ReceberCaixaRepositoryTest.class
})
public class FinanceiroTest {
    
}
