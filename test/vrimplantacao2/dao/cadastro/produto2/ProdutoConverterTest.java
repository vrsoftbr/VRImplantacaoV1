/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.produto2;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryTest.getProdutoIMP_ACEM;
import static vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryTest.getProdutoIMP_MOCA;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

@RunWith(MockitoJUnitRunner.class)
public class ProdutoConverterTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProdutoRepositoryProvider provider;
    
    public ProdutoConverterTest() {
    }

    @Test
    public void testConverterAnterior_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAnteriorVO actual = new ProdutoConverter(provider).converterImpEmAnterior(imp);
        
        assertEquals("17.020.00", actual.getCest());
        assertArrayEquals(new String[] { "TESTE", "LOJA 02", "12345" }, actual.getChave());
        assertNull(actual.getCodigoAtual());
        assertEquals(0, actual.getContadorImportacao());
        assertEquals(4.02d, actual.getCustocomimposto(), 0.01);
        assertEquals(3.65d, actual.getCustosemimposto(), 0.01);
        assertEquals("LEITE CONDENSADO CREMOSO MOCA LATA 395G", actual.getDescricao());
        assertEquals(568, actual.getEstoque(), 0.01);
        assertEquals(18d, actual.getIcmsAliq(), 0.1);
        assertEquals(20, actual.getIcmsCst());
        assertEquals(61.11d, actual.getIcmsReducao(), 0.01);
        assertEquals("12345", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(70, actual.getMargem(), 0.0);
        assertEquals("0402.99.00", actual.getNcm());
        assertEquals(71, actual.getPisCofinsCredito());
        assertEquals(7, actual.getPisCofinsDebito());
        assertEquals(101, actual.getPisCofinsNaturezaReceita());
        assertEquals(7.12d, actual.getPrecovenda(),0.01);
    }
    
    @Test
    public void testConverterAnterior_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAnteriorVO actual = new ProdutoConverter(provider).converterImpEmAnterior(imp);
        
        assertEquals("17.083.00", actual.getCest());
        assertArrayEquals(new String[] { "TESTE", "LOJA 02", "3214" }, actual.getChave());
        assertNull(actual.getCodigoAtual());
        assertEquals(0, actual.getContadorImportacao());
        assertEquals(14.02d, actual.getCustocomimposto(), 0.01);
        assertEquals(13.65d, actual.getCustosemimposto(), 0.01);
        assertEquals("ACEM BOVINO KG", actual.getDescricao());
        assertEquals(568, actual.getEstoque(), 0.01);
        assertEquals(18d, actual.getIcmsAliq(), 0.1);
        assertEquals(0, actual.getIcmsCst());
        assertEquals(0, actual.getIcmsReducao(), 0.01);
        assertEquals("3214", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(70, actual.getMargem(), 0.0);
        assertEquals("0210.20.00", actual.getNcm());
        assertEquals(73, actual.getPisCofinsCredito());
        assertEquals(6, actual.getPisCofinsDebito());
        assertEquals(121, actual.getPisCofinsNaturezaReceita());
        assertEquals(17.12d, actual.getPrecovenda(),0.01);
    }    
    
    @Test
    public void testConverterEANAnterior_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAnteriorEanVO actual = new ProdutoConverter(provider).converterAnteriorEAN(imp);
        
        assertEquals("7891000100103", actual.getEan());
        assertEquals("12345", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(1, actual.getQtdEmbalagem());
        assertEquals("UN", actual.getTipoEmbalagem());
        assertEquals(0, actual.getValor(), 0.01);
    }
    
    @Test
    public void testConverterEANAnterior_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAnteriorEanVO actual = new ProdutoConverter(provider).converterAnteriorEAN(imp);
        
        assertEquals("18", actual.getEan());
        assertEquals("3214", actual.getImportId());
        assertEquals("LOJA 02", actual.getImportLoja());
        assertEquals("TESTE", actual.getImportSistema());
        assertEquals(1, actual.getQtdEmbalagem());
        assertEquals("KG", actual.getTipoEmbalagem());
        assertEquals(0, actual.getValor(), 0.01);
    }
    
}
