/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.produto;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class OrganizadorIMPTest {
    
    public OrganizadorIMPTest() {
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

    @Test
    public void testOrganizarIds() throws Exception {
        System.out.println("organizarIds");
        MultiMap<String, ProdutoIMP> filtrados = null;
        OrganizadorIMP instance = null;
        instance.organizarIds(filtrados);
        fail("The test case is a prototype.");
    }

    @Test
    public void testOrganizarListagem() throws Exception {
        System.out.println("organizarListagem");
        List<ProdutoIMP> produtos = null;
        OrganizadorIMP instance = null;
        MultiMap<String, ProdutoIMP> expResult = null;
        MultiMap<String, ProdutoIMP> result = instance.organizarListagem(produtos);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testEliminarDuplicados() throws Exception {
        System.out.println("eliminarDuplicados");
        List<ProdutoIMP> produtos = null;
        OrganizadorIMP instance = null;
        MultiMap<String, ProdutoIMP> expResult = null;
        MultiMap<String, ProdutoIMP> result = instance.eliminarDuplicados(produtos);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSepararBalancaENormais() throws Exception {
        System.out.println("separarBalancaENormais");
        MultiMap<String, ProdutoIMP> filtrados = null;
        MultiMap<String, ProdutoIMP> balanca = null;
        MultiMap<String, ProdutoIMP> normais = null;
        OrganizadorIMP instance = null;
        instance.separarBalancaENormais(filtrados, balanca, normais);
        fail("The test case is a prototype.");
    }
    
}
