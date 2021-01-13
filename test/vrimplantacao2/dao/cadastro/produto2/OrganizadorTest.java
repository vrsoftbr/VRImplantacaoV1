package vrimplantacao2.dao.cadastro.produto2;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
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
    public void testOrganizarIds() throws Exception {
    }

    @Test
    public void testOrganizarListagem() throws Exception {
    }

    @Test
    public void testEliminarDuplicados() throws Exception {
        System.out.print("OrganizadorTest.testEliminarDuplicados...");

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
        System.out.print("OrganizadorTest.testSepararProdutosBalancaMANTENDO_PLU...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("1", "000156", "KG", true));
        imports.add(newProduto("A222", "3", "UN", true));
        imports.add(newProduto("222", "AA443", "KG", true));
        imports.add(newProduto("156", "0004", "UN", true));
        imports.add(newProduto("356", "00004", "KG", true));
        imports.add(newProduto("157", "2", "KG", false));
        imports.add(newProduto("1345", "7891000100103", "KG", false));
        imports.add(newProduto("1346", "1346", "UN", false));

        List<ProdutoIMP> balanca = new Organizador(repository).separarProdutosBalanca(imports, true);

        assertEquals(6, balanca.size());
        assertEquals(2, imports.size());//Produtos não retornados, são mantidos na lista original.
        assertEquals("157", balanca.get(0).getImportId());//2
        assertEquals("A222", balanca.get(1).getImportId());//3
        assertEquals("156", balanca.get(2).getImportId());//0004
        assertEquals("1", balanca.get(3).getImportId());//000156
        assertEquals("356", balanca.get(4).getImportId());//00004
        assertEquals("222", balanca.get(5).getImportId());//AA443

        System.out.println("OK");
    }

    @Test
    public void testSepararProdutosBalancaMANTENDO_IDS() throws Exception {
        System.out.print("OrganizadorTest.testSepararProdutosBalancaMANTENDO_IDS...");

        List<ProdutoIMP> imports = new ArrayList<>();

        imports.add(newProduto("1", "000156", "KG", true));
        imports.add(newProduto("A222", "3", "UN", true));
        imports.add(newProduto("222", "AA443", "KG", true));
        imports.add(newProduto("156", "0004", "UN", true));
        imports.add(newProduto("356", "00004", "KG", true));
        imports.add(newProduto("157", "2", "KG", false));
        imports.add(newProduto("1345", "7891000100103", "KG", false));
        imports.add(newProduto("1346", "1346", "UN", false));

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

    private ProdutoIMP newProduto(String id, String ean, String unidade, boolean pesavel) {
        ProdutoIMP imp = new ProdutoIMP();
        imp.setImportSistema(repository.getSistema());
        imp.setImportLoja(repository.getLoja());
        imp.setImportId(id);
        imp.setEan(ean);
        imp.setTipoEmbalagem(unidade);
        imp.seteBalanca(pesavel);
        return imp;
    }

}
