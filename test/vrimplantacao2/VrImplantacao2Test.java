package vrimplantacao2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import vrimplantacao2.dao.cadastro.CadastroTest;
import vrimplantacao2.utils.UtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    UtilsTest.class,
    CadastroTest.class
})
public class VrImplantacao2Test {
    
}
