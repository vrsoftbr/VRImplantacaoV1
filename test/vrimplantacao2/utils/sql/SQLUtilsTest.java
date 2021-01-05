package vrimplantacao2.utils.sql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leandro
 */
public class SQLUtilsTest {

    @Test
    public void testQuebrarSqlEmMesesDATA_IGUAL() throws Exception {
        System.out.print("SQLUtilsTest.testQuebrarSqlEmMesesDATA_IGUAL()...");
        
        String sql = "select teste from ts where data >= '{DATA_INICIO}' and data < '{DATA_TERMINO}'";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        List<String> stms = SQLUtils.quebrarSqlEmMeses(sql, format.parse("2018-01-01"), format.parse("2018-01-01"), format);
        
        assertEquals(1, stms.size());
        assertEquals("select teste from ts where data >= '2018-01-01' and data < '2018-01-01'", stms.get(0));
        
        System.out.println("OK");
    }

    @Test
    public void testQuebrarSqlEmMesesDATA_MESMO_MES() throws Exception {
        System.out.print("SQLUtilsTest.testQuebrarSqlEmMesesDATA_MESMO_MES()...");
        
        String sql = "select teste from ts where data >= '{DATA_INICIO}' and data < '{DATA_TERMINO}'";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        List<String> stms = SQLUtils.quebrarSqlEmMeses(sql, format.parse("2018-01-05"), format.parse("2018-01-15"), format);
        
        assertEquals(1, stms.size());
        assertEquals("select teste from ts where data >= '2018-01-05' and data < '2018-01-15'", stms.get(0));
        
        System.out.println("OK");
    }
    
    @Test
    public void testQuebrarSqlEmMesesDATA_3_MES() throws Exception {
        System.out.print("SQLUtilsTest.testQuebrarSqlEmMesesDATA_3_MES()...");
        
        String sql = "select teste from ts where data >= '{DATA_INICIO}' and data < '{DATA_TERMINO}'";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        List<String> stms = SQLUtils.quebrarSqlEmMeses(sql, format.parse("2018-01-05"), format.parse("2018-03-15"), format);
        
        assertEquals(3, stms.size());
        assertEquals("select teste from ts where data >= '2018-01-05' and data < '2018-02-05'", stms.get(0));
        assertEquals("select teste from ts where data >= '2018-02-06' and data < '2018-03-06'", stms.get(1));
        assertEquals("select teste from ts where data >= '2018-03-07' and data < '2018-03-15'", stms.get(2));
        
        System.out.println("OK");
    }
    
    @Test
    public void testQuebrarSqlEmMesesDATA_2_MES() throws Exception {
        System.out.print("SQLUtilsTest.testQuebrarSqlEmMesesDATA_2_MES()...");
        
        String sql = "select teste from ts where data >= '{DATA_INICIO}' and data < '{DATA_TERMINO}'";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        List<String> stms = SQLUtils.quebrarSqlEmMeses(sql, format.parse("2018-01-05"), format.parse("2018-03-06"), format);
        
        assertEquals(2, stms.size());
        assertEquals("select teste from ts where data >= '2018-01-05' and data < '2018-02-05'", stms.get(0));
        assertEquals("select teste from ts where data >= '2018-02-06' and data < '2018-03-06'", stms.get(1));
        
        System.out.println("OK");
    }
    
    @Test
    public void testQuebrarSqlEmMesesStringTest() throws Exception {
        System.out.print("SQLUtilsTest.testQuebrarSqlEmMesesStringTest()...");
        
        String sql = "log001venda{DATA_INICIO}";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        List<String> stms = SQLUtils.quebrarSqlEmMeses(sql, format.parse("2018-01-01"), format.parse("2018-03-30"), new SimpleDateFormat("MMyy"));
        
        assertEquals(3, stms.size());
        assertEquals("log001venda0118", stms.get(0));
        assertEquals("log001venda0218", stms.get(1));
        assertEquals("log001venda0318", stms.get(2));
        
        System.out.println("OK");
    }
    
    @Test
    public void testQuebrarSqlEmMesesStringTest2() throws Exception {
        System.out.print("SQLUtilsTest.testQuebrarSqlEmMesesStringTest2()...");
        
        String sql = "log001venda{DATA_INICIO}";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        
        List<String> stms = SQLUtils.quebrarSqlEmMeses(sql, format.parse("2020-08-01"), format.parse("2020-08-31"), new SimpleDateFormat("MMyy"));
        
        assertEquals(1, stms.size());
        assertEquals("log001venda0820", stms.get(0));
        
        System.out.println("OK");
    }
    
    @Test
    public void testIntervalosMensais() throws Exception {
        System.out.print("SQLUtilsTest.testIntervalosMensais()...");
        
        List<SQLUtils.Intervalo> intervalos = SQLUtils.intervalosMensais(
                FORMAT.parse("2019-11-24"),
                FORMAT.parse("2020-04-14")
        );
        
        assertEquals(6, intervalos.size());
        testIntervalor(intervalos.get(0), "2019-11-24", "2019-11-30");
        testIntervalor(intervalos.get(1), "2019-12-01", "2019-12-31");
        testIntervalor(intervalos.get(2), "2020-01-01", "2020-01-31");
        testIntervalor(intervalos.get(3), "2020-02-01", "2020-02-29");
        testIntervalor(intervalos.get(4), "2020-03-01", "2020-03-31");
        testIntervalor(intervalos.get(5), "2020-04-01", "2020-04-14");
        
        System.out.println("OK");
    }
    
    private SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public void testIntervalor(SQLUtils.Intervalo intervalo, String dataInicial, String dataFinal) throws ParseException {
        assertEquals(FORMAT.format(intervalo.dataInicial), dataInicial);
        assertEquals(FORMAT.format(intervalo.dataFinal),dataFinal);
    }
    
}
