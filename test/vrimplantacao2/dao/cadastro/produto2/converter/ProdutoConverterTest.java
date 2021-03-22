package vrimplantacao2.dao.cadastro.produto2.converter;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepository;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import static vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryTest.getProdutoIMP_ACEM;
import static vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryTest.getProdutoIMP_MOCA;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.TipoEmbalagem;
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
        ProdutoAnteriorVO actual = new ProdutoConverter(provider).converterEmAnterior(imp);
        
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
        ProdutoAnteriorVO actual = new ProdutoConverter(provider).converterEmAnterior(imp);
        
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

    @Test
    public void testConverterEAN_MOCA() throws Exception {
        ProdutoIMP imp = getProdutoIMP_MOCA();
        ProdutoAutomacaoVO actual = new ProdutoConverter(provider).converterEAN(imp, 7891000100103L, TipoEmbalagem.UN);
        assertEquals(7891000100103l, actual.getCodigoBarras());
        assertEquals(TipoEmbalagem.UN, actual.getTipoEmbalagem());
        assertEquals(1, actual.getQtdEmbalagem());
        assertFalse(actual.isDun14());
        assertEquals(-1, actual.getId()); //Não é para retornar nada
        assertNull(actual.getProduto());
    }
    
    @Test
    public void testConverterEAN_ACEM() throws Exception {
        ProdutoIMP imp = getProdutoIMP_ACEM();
        ProdutoAutomacaoVO actual = new ProdutoConverter(provider).converterEAN(imp, 18, TipoEmbalagem.KG);
        assertEquals(18, actual.getCodigoBarras());
        assertEquals(TipoEmbalagem.KG, actual.getTipoEmbalagem());
        assertEquals(1, actual.getQtdEmbalagem());
        assertFalse(actual.isDun14());
        assertEquals(-1, actual.getId()); //Não é para retornar nada
        assertNull(actual.getProduto());
    }
    
    @Test
    public void testConvertPisCofins_INVALIDO() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(-1);
        imp.setPiscofinsCstCredito(-1);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(7, actual.getPisCofinsDebito().getCst());
        assertEquals(71, actual.getPisCofinsCredito().getCst());
        assertEquals(999, actual.getPisCofinsNaturezaReceita().getCodigo());
        
    }
    
    @Test
    public void testConvertPisCofins_INVALIDO_2() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(99);
        imp.setPiscofinsCstCredito(99);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(49, actual.getPisCofinsDebito().getCst());
        assertEquals(99, actual.getPisCofinsCredito().getCst());
        
    }
    
    @Test
    public void testConvertPisCofins_INVALIDO_3() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(49);
        imp.setPiscofinsCstCredito(49);
        imp.setPiscofinsNaturezaReceita(0);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(49, actual.getPisCofinsDebito().getCst());
        assertEquals(99, actual.getPisCofinsCredito().getCst());
        
    }
    
    private void testNaturezaReceita(ProdutoRepository repository, int cstDebito, int naturezaReceita, int expected) throws Exception {
        
        NaturezaReceitaVO nat = repository.getNaturezaReceita(cstDebito, naturezaReceita);        
        assertEquals(expected, nat.getCodigo());
        
    }
    
    @Test
    public void testGetNaturezaReceita() throws Exception {
        ProdutoRepository repository = new ProdutoRepository(provider);
        
        assertNull(repository.getNaturezaReceita(1, 2));
        testNaturezaReceita(repository, 2, 403, 403);
        testNaturezaReceita(repository, 2, 15, 403);
        testNaturezaReceita(repository, 3, 940, 940);
        testNaturezaReceita(repository, 3, 0, 940);
        testNaturezaReceita(repository, 4, 403, 403);
        testNaturezaReceita(repository, 4, 48, 403);
        testNaturezaReceita(repository, 5, 409, 409);
        testNaturezaReceita(repository, 5, 789, 409);
        testNaturezaReceita(repository, 6, 999, 999);
        testNaturezaReceita(repository, 6, 121, 121);
        testNaturezaReceita(repository, 6, 10, 999);
        testNaturezaReceita(repository, 7, 999, 999);
        testNaturezaReceita(repository, 7, 156, 999);
        testNaturezaReceita(repository, 8, 999, 999);
        testNaturezaReceita(repository, 8, 741, 999);
        testNaturezaReceita(repository, 9, 999, 999);
        testNaturezaReceita(repository, 9, 359, 999);
        
    }
    
    @Test
    public void testConvertPisCofins_VALIDO_C_NATUREZA_RECEITA() throws Exception {
        
        ProdutoIMP imp = getProdutoIMP_MOCA();
        
        imp.setPiscofinsCstDebito(4);
        imp.setPiscofinsCstCredito(70);
        imp.setPiscofinsNaturezaReceita(403);
        
        ProdutoVO actual = new ProdutoVO();        
        new ProdutoRepository(provider).convertPisCofins(imp, actual);
        assertEquals(4, actual.getPisCofinsDebito().getCst());
        assertEquals(70, actual.getPisCofinsCredito().getCst());
        assertEquals(403, actual.getPisCofinsNaturezaReceita().getCodigo());
        
    }
    
}
