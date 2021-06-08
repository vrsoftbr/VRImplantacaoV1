/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;

/**
 *
 * @author Leandro
 */
public class CestDAOTest {
    
    public CestDAOTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of validar method, of class CestDAO.
     */
    @Test
    public void testParse() {
        System.out.println("Parse...");
        
        CestVO cest = CestDAO.parse("10.249.69");
        assertEquals(10, cest.getCest1());
        assertEquals(249, cest.getCest2());
        assertEquals(69, cest.getCest3());
        
        cest = CestDAO.parse("00.056.78");
        assertEquals(0, cest.getCest1());
        assertEquals(56, cest.getCest2());
        assertEquals(78, cest.getCest3());
        
        cest = CestDAO.parse("00.000.34");
        assertEquals(0, cest.getCest1());
        assertEquals(0, cest.getCest2());
        assertEquals(34, cest.getCest3());    
        
        cest = CestDAO.parse("00.000.00");
        assertEquals(0, cest.getCest1());
        assertEquals(0, cest.getCest2());
        assertEquals(0, cest.getCest3());
        
        cest = CestDAO.parse("");
        assertEquals(-1, cest.getCest1());
        assertEquals(-1, cest.getCest2());
        assertEquals(-1, cest.getCest3());
        
        cest = CestDAO.parse(null);
        assertEquals(-1, cest.getCest1());
        assertEquals(-1, cest.getCest2());
        assertEquals(-1, cest.getCest3());

    }
    
    @Ignore
    @Test
    public void testGetCestValido() throws Exception {
        CestDAO dao = new CestDAO();
        
        NcmVO ncm = new NcmVO(9401,20,0);
        
        Conexao.abrirConexao("localhost", 5432, "vr_cantinho_fruta", "postgres", "postgres");
        
        CestVO cest = dao.getCestValido(null);
        assertEquals(-1, cest.getCest1());
        assertEquals(-1, cest.getCest2());
        assertEquals(-1, cest.getCest3());
        
        cest = dao.getCestValido("");
        assertEquals(-1, cest.getCest1());
        assertEquals(-1, cest.getCest2());
        assertEquals(-1, cest.getCest3());
        
        cest = dao.getCestValido("01.085.00");
        assertEquals(1, cest.getCest1());
        assertEquals(85, cest.getCest2());
        assertEquals(0, cest.getCest3());
        
        cest = dao.getCestValido("10.085.00");
        assertEquals(-1, cest.getCest1());
        assertEquals(-1, cest.getCest2());
        assertEquals(-1, cest.getCest3());
        
        cest = dao.getCestValido("01.085.00");
        assertEquals(1, cest.getCest1());
        assertEquals(85, cest.getCest2());
        assertEquals(0, cest.getCest3());
    }
    
}
