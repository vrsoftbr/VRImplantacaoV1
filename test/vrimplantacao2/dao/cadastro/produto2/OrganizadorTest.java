package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizadorTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProdutoRepository repository;

    @Before
    public void init() {
        when(repository.getLoja()).thenReturn("LOJA 02");
        when(repository.getSistema()).thenReturn("TESTE");
    }

    public OrganizadorTest() {
    }

    @Test
    public void testOrganizarListagem() throws Exception {
        System.out.print("OrganizadorTest.testOrganizarListagem()...");
        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("156", "178", "UN", true, false));
        imports.add(newProduto("1", "000156", "KG", true, false));
        imports.add(newProduto("A222", "3", "UN", true, false));
        imports.add(newProduto("222", "AA443", "KG", true, false));
        imports.add(newProduto("156", "0004", "UN", true, false));
        imports.add(newProduto("356", "00004", "KG", true, false));
        imports.add(newProduto("157", "2", "KG", false, false));
        imports.add(newProduto("1345", "7891000100103", "KG", false, false));
        imports.add(newProduto("1346", "1346", "UN", false, false));
        imports.add(newProduto("156", "005", "UN", true, false));
        imports.add(newProduto("1", "000156", "KG", false, false));
        imports.add(newProduto("A222", "0000003", "UN", false, false));
        imports.add(newProduto("222", "17891000100103", "KG", false, false));
        imports.add(newProduto("157", "2", "KG", false, false));//
        imports.add(newProduto("0000156", "0004", "UN", false, false));
        imports.add(newProduto("356", "00004567892", "KG", false, false));
        imports.add(newProduto("1345", "7891000100103", "KG", false, false));//
        imports.add(newProduto("1346", "AAA1346", "UN", false, false));
                
        
        
        when(repository.getOpcoes()).thenReturn(new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA
        )));
        
        List<ProdutoIMP> result = new Organizador(repository).organizarListagem(imports);
        
        assertTrue(imports.isEmpty());
        assertEquals(15, result.size());
        
        for (ProdutoIMP imp: result) {
            System.out.println(String.format("ID: %s EAN: %s Bal:%s ManEAN: %s", 
                    imp.getImportId(), imp.getEan(),
                    (imp.isBalanca() || imp.getTipoEmbalagem().equals("KG")),
                    imp.isManterEAN()));
        }
        
        System.out.println("OK");
    }

    @Test
    public void testEliminarDuplicados() throws Exception {
        System.out.print("OrganizadorTest.testEliminarDuplicados()...");

        List<ProdutoIMP> imports = new ArrayList<>();
        ProdutoIMP imp1 = ProdutoRepositoryTest.getProdutoIMP_MOCA();
        ProdutoIMP imp2 = ProdutoRepositoryTest.getProdutoIMP_MOCA2();
        ProdutoIMP imp3 = ProdutoRepositoryTest.getProdutoIMP_ACEM();
        imports.add(imp1);
        imports.add(imp2);
        imports.add(imp3);

        List<ProdutoIMP> results;
        results = new Organizador(repository).eliminarDuplicados(imports);
        assertEquals(2, results.size());

        imp2.setEan("00007891000100103");
        results = new Organizador(repository).eliminarDuplicados(imports);
        assertEquals(3, results.size());

        imp2.setEan("17891000100103");
        results = new Organizador(repository).eliminarDuplicados(imports);
        assertEquals(3, results.size());

        System.out.println("OK");
    }

    @Test
    public void testSepararProdutosBalancaMANTENDO_PLU() throws Exception {
        System.out.print("OrganizadorTest.testSepararProdutosBalancaMANTENDO_PLU()...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("156", "178", "UN", true, false));
        imports.add(newProduto("1", "000156", "KG", true, false));
        imports.add(newProduto("A222", "3", "UN", true, false));
        imports.add(newProduto("222", "AA443", "KG", true, false));
        imports.add(newProduto("156", "0004", "UN", true, false));
        imports.add(newProduto("356", "00004", "KG", true, false));
        imports.add(newProduto("157", "2", "KG", false, false));
        imports.add(newProduto("1345", "7891000100103", "KG", false, false));
        imports.add(newProduto("1346", "1346", "UN", false, false));
        imports.add(newProduto("156", "005", "UN", true, false));

        List<ProdutoIMP> balanca = new Organizador(repository).separarProdutosBalanca(imports, true);

        assertEquals(8, balanca.size());
        assertEquals(2, imports.size());//Produtos não retornados, são mantidos na lista original.
        assertEquals("157", balanca.get(0).getImportId());//2
        assertEquals("A222", balanca.get(1).getImportId());//3
        assertEquals("156", balanca.get(2).getImportId());//0004
        assertEquals("0004", balanca.get(2).getEan());//0004
        assertEquals("156", balanca.get(3).getImportId());//0004
        assertEquals("005", balanca.get(3).getEan());//0004
        assertEquals("1", balanca.get(4).getImportId());//000156
        assertEquals("156", balanca.get(5).getImportId());//000156
        assertEquals("178", balanca.get(5).getEan());//000156
        assertEquals("356", balanca.get(6).getImportId());//00004
        assertEquals("222", balanca.get(7).getImportId());//AA443

        System.out.println("OK");
    }

    @Test
    public void testSepararProdutosBalancaMANTENDO_IDS() throws Exception {
        System.out.print("OrganizadorTest.testSepararProdutosBalancaMANTENDO_IDS()...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("1", "000156", "KG", true, false));
        imports.add(newProduto("A222", "3", "UN", true, false));
        imports.add(newProduto("222", "AA443", "KG", true, false));
        imports.add(newProduto("156", "0004", "UN", true, false));
        imports.add(newProduto("356", "00004", "KG", true, false));
        imports.add(newProduto("157", "2", "KG", false, false));
        imports.add(newProduto("1345", "7891000100103", "KG", false, false));
        imports.add(newProduto("1346", "1346", "UN", false, false));

        List<ProdutoIMP> balanca = new Organizador(repository).separarProdutosBalanca(imports, false);

        assertEquals(5, balanca.size());
        assertEquals(3, imports.size());//Produtos não retornados, são mantidos na lista original.
        assertEquals("1", balanca.get(0).getImportId());//2
        assertEquals("156", balanca.get(1).getImportId());//3
        assertEquals("157", balanca.get(2).getImportId());//0004
        assertEquals("222", balanca.get(3).getImportId());//000156
        assertEquals("356", balanca.get(4).getImportId());//AA443

        System.out.println("OK");
    }
    
    @Test
    public void testSepararProdutosManterEan_MANTER_EAN_INDIVIDUAL() throws Exception {
        System.out.print("OrganizadorTest.testSepararProdutosManterEan_MANTER_EAN_INDIVIDUAL()...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("1", "000156", "KG", false, true));
        imports.add(newProduto("A222", "0000003", "UN", false, true));
        imports.add(newProduto("222", "AA443", "KG", false, true));
        imports.add(newProduto("157", "2", "KG", false, false));
        imports.add(newProduto("156", "0004", "UN", false, true));
        imports.add(newProduto("356", "00004", "KG", false, true));
        imports.add(newProduto("1345", "7891000100103", "KG", false, true));
        imports.add(newProduto("1346", "1346", "UN", false, false));

        List<ProdutoIMP> manterEan = new Organizador(repository).separarManterEAN(imports, false);
        
        /*
        valids 000156,3,0004 -> 3,0004,00156
        outros AA443,2,00004,7891000100103,1346
        */

        assertEquals(3, manterEan.size());
        assertEquals(5, imports.size());//Produtos não retornados, são mantidos na lista original.
        assertEquals("A222", manterEan.get(0).getImportId());//2
        assertEquals("156", manterEan.get(1).getImportId());//3
        assertEquals("1", manterEan.get(2).getImportId());//0004

        System.out.println("OK");
    }
    
    @Test
    public void testSepararProdutosManterEan_MANTER_EAN_OPCAO() throws Exception {
        System.out.print("OrganizadorTest.testSepararProdutosManterEan_MANTER_EAN_OPCAO()...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("1", "000156", "KG", false, false));
        imports.add(newProduto("A222", "0000003", "UN", false, false));
        imports.add(newProduto("222", "AA443", "KG", false, false));
        imports.add(newProduto("157", "2", "KG", false, false));
        imports.add(newProduto("156", "0004", "UN", false, false));
        imports.add(newProduto("356", "00004", "KG", false, false));
        imports.add(newProduto("1345", "7891000100103", "KG", false, false));
        imports.add(newProduto("1346", "1346", "UN", false, false));

        List<ProdutoIMP> manterEan = new Organizador(repository).separarManterEAN(imports, true);
        
        /*
        valids 000156,0000003,2,0004,1346 -> 2,0000003,0004,000156,1346
        outros AA443,00004,7891000100103
        */

        assertEquals(5, manterEan.size());
        assertEquals(3, imports.size());//Produtos não retornados, são mantidos na lista original.
        assertEquals("157", manterEan.get(0).getImportId());
        assertEquals("A222", manterEan.get(1).getImportId());
        assertEquals("156", manterEan.get(2).getImportId());
        assertEquals("1", manterEan.get(3).getImportId());
        assertEquals("1346", manterEan.get(4).getImportId());

        System.out.println("OK");
    }
    
    @Test
    public void testSepararIdsValidos() throws Exception {
        System.out.print("OrganizadorTest.testSepararIdsValidos()...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("1", "000156", "KG", false, false));
        imports.add(newProduto("A222", "0000003", "UN", false, false));
        imports.add(newProduto("222", "17891000100103", "KG", false, false));
        imports.add(newProduto("157", "2", "KG", false, false));
        imports.add(newProduto("0000156", "0004", "UN", false, false));
        imports.add(newProduto("356", "00004567892", "KG", false, false));
        imports.add(newProduto("1345", "7891000100103", "KG", false, false));
        imports.add(newProduto("1346", "AAA1346", "UN", false, false));

        List<ProdutoIMP> normais = new Organizador(repository).separarIdsValidos(imports);
        
        /*
        * * IDs válidos
        * * EANs válidos
        valids 000156,0000003,2,0004,1346 -> 2,0000003,0004,000156,1346
        outros AA443,00004,7891000100103
        */

        assertEquals(7, normais.size());
        assertEquals(1, imports.size());//Produtos não retornados, são mantidos na lista original.
        assertEquals("1", normais.get(0).getImportId());
        assertEquals("0000156", normais.get(1).getImportId());
        assertEquals("157", normais.get(2).getImportId());
        assertEquals("222", normais.get(3).getImportId());
        assertEquals("356", normais.get(4).getImportId());
        assertEquals("1345", normais.get(5).getImportId());
        assertEquals("1346", normais.get(6).getImportId());

        System.out.println("OK");
    }

    private ProdutoIMP newProduto(String id, String ean, String unidade, boolean pesavel, boolean manterEan) {
        ProdutoIMP imp = new ProdutoIMP();
        imp.setImportSistema(repository.getSistema());
        imp.setImportLoja(repository.getLoja());
        imp.setImportId(id);
        imp.setEan(ean);
        imp.setTipoEmbalagem(unidade);
        imp.seteBalanca(pesavel);
        imp.setManterEAN(manterEan);
        return imp;
    }

}
