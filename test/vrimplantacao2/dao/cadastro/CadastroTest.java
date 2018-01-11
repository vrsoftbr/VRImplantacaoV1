package vrimplantacao2.dao.cadastro;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialIDStackTest;
import vrimplantacao2.dao.cadastro.cliente.ClienteRepositoryTest;
import vrimplantacao2.dao.cadastro.financeiro.FinanceiroTest;
import vrimplantacao2.dao.cadastro.fiscal.FiscalTest;
import vrimplantacao2.dao.cadastro.mercadologico.MercadologicoRepositoryTest;
import vrimplantacao2.dao.cadastro.produto2.ProdutoIDStackTest;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    FinanceiroTest.class,
    ProdutoIDStackTest.class,
    ProdutoRepositoryTest.class,
    ClientePreferencialIDStackTest.class,
    ClienteRepositoryTest.class,
    MercadologicoRepositoryTest.class,
    FiscalTest.class
})
public class CadastroTest {
    
}
