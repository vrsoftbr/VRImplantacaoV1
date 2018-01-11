package vrimplantacao2.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import vrimplantacao2.utils.arquivo.csv.ArquivoCSVTest;
import vrimplantacao2.utils.arquivo.csv.CsvTokennerTest;
import vrimplantacao2.utils.collection.IDStackTest;
import vrimplantacao2.utils.multimap.KeyListTest;
import vrimplantacao2.utils.multimap.MultiMapTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    IDStackTest.class,
    MathUtilsTest.class,
    MultiMapTest.class,
    KeyListTest.class,
    ArquivoCSVTest.class,
    CsvTokennerTest.class
})
public class UtilsTest {
    
}
