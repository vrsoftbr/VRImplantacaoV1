package vrimplantacao2.dao.cadastro.mercadologico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

@RunWith(MockitoJUnitRunner.class)
public class ImportadorMercadologicoNormalTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    MercadologicoRepository repository;
    
    public ImportadorMercadologicoNormalTest() {
    }
    
    @Test
    public void filtrarRepetidos() throws Exception {
        List<MercadologicoIMP> imp = new ArrayList<>();
        imp.add(get("t1"));
        imp.add(get("t1","t2"));
        imp.add(get("t1","t2","t3"));
        imp.add(get("t1","t2","t3","t4"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("AD","t2","t3","t4","5"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("t1","t2","t3","445","5"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("GT","t2","FD","t4","5"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("A","B","C"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("GT","t2","FD","t4","5"));
        imp.add(get("t1","t2","t3","t4","789"));
        imp.add(get("t1","t2","t3","t4","5"));
        imp.add(get("t1","t2","t3","t4","789"));
        imp.add(get("t1","t2","t3","t4","5"));
        ImportadorMercadologicoNormal importador = new ImportadorMercadologicoNormal(repository);
        assertEquals(10, importador.filtrarRepetidos(imp).size());
    }

    @Test
    public void salvar() throws Exception {
        final List<MercadologicoNivelIMP> resultados = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<MercadologicoNivelIMP> argumentAt = 
                        (List<MercadologicoNivelIMP>) invocation.getArgument(0, List.class);
                for (MercadologicoNivelIMP imp: argumentAt) {
                    resultados.add(imp);
                }
                return null;
            }
        }).when(repository).salvar(new ArrayList<MercadologicoNivelIMP>(), new HashSet<OpcaoProduto>());
        
        List<MercadologicoIMP> imp = new ArrayList<>();
        imp.add(get("t1","t2","t3","t4","t5", "extra"));
        imp.add(get("t1"));
        imp.add(get("t1","t2"));
        imp.add(get("t1","A1", "A2"));
        imp.add(get("t1","t2","t3","t4","t5"));
        imp.add(get("t1","t2","t3","t4","B1"));
        imp.add(get("t1","t2","t3","t4","t5"));
        imp.add(get("t1","A1", "A2", "C1"));
        imp.add(get("t1","A1", "A2"));
        imp.add(get("t1","A1", "A4"));
        imp.add(get("t1","t2","t3","t4","B2"));
        imp.add(get("t1","t2","t3"));
        imp.add(get("t1","t2","t3","t4"));
        imp.add(get("t1","t2","t3","t4","t5", "extra"));
        imp.add(get("t1","A1", "A3"));
        ImportadorMercadologicoNormal importador = new ImportadorMercadologicoNormal(repository);
        importador.salvar(imp, EnumSet.noneOf(OpcaoProduto.class));
        
        assertEquals(1, resultados.size());
        MercadologicoNivelIMP merc1 = resultados.get(0);
        assertEquals(2, merc1.getNiveis().size());
        
        {
            MercadologicoNivelIMP merc2;
            merc2 = merc1.getNiveis().get("t2");
            assertEquals(1, merc2.getNiveis().size());
            {
                MercadologicoNivelIMP merc3 = merc2.getNiveis().get("t3");
                assertEquals(1, merc3.getNiveis().size());
                {                    
                    MercadologicoNivelIMP merc4 = merc3.getNiveis().get("t4");
                    assertEquals(3, merc4.getNiveis().size());
                }
            }            
        }
        {
            MercadologicoNivelIMP merc2 = merc1.getNiveis().get("A1");
            assertEquals(3, merc2.getNiveis().size());      
            MercadologicoNivelIMP merc3;
            {                
                MercadologicoNivelIMP merc4 = merc2.getNiveis().get("A2");
                assertEquals(1, merc4.getNiveis().size());
            }
            merc3 = merc2.getNiveis().get("A3");
            assertTrue(merc3.getNiveis().isEmpty());
            merc3 = merc2.getNiveis().get("A4");
            assertTrue(merc3.getNiveis().isEmpty());
        }
    }
    
    @Test
    public void filtrarPorNivel() throws Exception {
        Map<MercadologicoKey, MercadologicoIMP> map = new LinkedHashMap<>();
        
        put(map,"t1","t2","t3","t4","5");
        put(map,"AD","t2","t3","t4","5");
        put(map,"t1","t2","t3","445","5");
        put(map,"A","B","C");
        put(map,"GT","t2","FD","t4","5");
        put(map,"GT","t3","FD","t4","5");
        put(map,"GT","t3","UH","t4","5");
        put(map,"GT","t3","UH","AA");
        put(map,"t1","t2","t3","t4","789");
        put(map,"t1","t2","t3","t4","78");
        put(map,"t1","t2","t3","t4","78", "extra");
        put(map,"t1");
        
        ImportadorMercadologicoNormal importador = new ImportadorMercadologicoNormal(repository);
        assertEquals(4, importador.filtrarPorNivel(map, 1).size());
        assertEquals(5, importador.filtrarPorNivel(map, 2).size());
        assertEquals(6, importador.filtrarPorNivel(map, 3).size());
        assertEquals(7, importador.filtrarPorNivel(map, 4).size());
        assertEquals(8, importador.filtrarPorNivel(map, 5).size());
        
    }
    
    @Test
    public void converterMercadologico() {
        ImportadorMercadologicoNormal importador = new ImportadorMercadologicoNormal(repository);
        MercadologicoIMP input = get("N1","N2","N3","N4","N5");
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 0);
            assertEquals("N1", actual.getId());
            assertEquals("DESC N1", actual.getDescricao());
        }
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 1);
            assertEquals("N1", actual.getId());
            assertEquals("DESC N1", actual.getDescricao());
        }
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 2);
            assertEquals("N2", actual.getId());
            assertEquals("DESC N2", actual.getDescricao());
        }
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 3);
            assertEquals("N3", actual.getId());
            assertEquals("DESC N3", actual.getDescricao());
        }
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 4);
            assertEquals("N4", actual.getId());
            assertEquals("DESC N4", actual.getDescricao());
        }
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 5);
            assertEquals("N5", actual.getId());
            assertEquals("DESC N5", actual.getDescricao());
        }
        
        {
            MercadologicoNivelIMP actual = importador.converterMercadologico(input, 6);
            assertEquals("N5", actual.getId());
            assertEquals("DESC N5", actual.getDescricao());
        }
    }
    
    @Test
    public void relacionarMercadologicosFilhosAosPais() {
        Map<MercadologicoKey, MercadologicoNivelIMP> mercadologicosPai = new TreeMap<>();
        Map<MercadologicoKey, MercadologicoNivelIMP> mercadologicosFilho = new TreeMap<>();
        
        MercadologicoKey[] cFil = {
            MercadologicoKey.filterAndBuildKey("1","2"),
            MercadologicoKey.filterAndBuildKey("1","3")
        };
        
        mercadologicosFilho.put(cFil[0], new MercadologicoNivelIMP("2", "DESC 2"));
        mercadologicosFilho.put(cFil[1], new MercadologicoNivelIMP("3", "DESC 3"));
        
        MercadologicoKey[] cPai = {
            MercadologicoKey.filterAndBuildKey("1"),
            MercadologicoKey.filterAndBuildKey("2")
        };
        
        mercadologicosPai.put(cPai[0], new MercadologicoNivelIMP("1", "PAI 1"));
        mercadologicosPai.put(cPai[1], new MercadologicoNivelIMP("2", "PAI 2"));
        
        assertEquals(0, mercadologicosPai.get(cPai[0]).getNiveis().size());
        assertEquals(0, mercadologicosPai.get(cPai[1]).getNiveis().size());
        assertNull(mercadologicosFilho.get(cFil[0]).getMercadologicoPai());
        assertNull(mercadologicosFilho.get(cFil[1]).getMercadologicoPai());
        
        ImportadorMercadologicoNormal importador = new ImportadorMercadologicoNormal(repository);
        importador.relacionarMercadologicosFilhosAosPais(mercadologicosFilho, mercadologicosPai, 1);
        
        assertEquals(2, mercadologicosPai.get(cPai[0]).getNiveis().size());
        assertEquals(0, mercadologicosPai.get(cPai[1]).getNiveis().size());
        assertEquals(mercadologicosPai.get(cPai[0]), mercadologicosFilho.get(cFil[0]).getMercadologicoPai());
        assertEquals(mercadologicosPai.get(cPai[0]), mercadologicosFilho.get(cFil[1]).getMercadologicoPai());
    }
    
    private MercadologicoIMP get(String... ids) {
        MercadologicoIMP imp = new MercadologicoIMP();
        imp.setImportSistema("TESTE");
        imp.setImportLoja("1");
        switch(ids.length) {
            default:
                imp.setMerc5ID(ids[4]);
                imp.setMerc5Descricao("DESC " + ids[4]);
            case 4:
                imp.setMerc4ID(ids[3]);
                imp.setMerc4Descricao("DESC " + ids[3]);
            case 3:
                imp.setMerc3ID(ids[2]);
                imp.setMerc3Descricao("DESC " + ids[2]);
            case 2:
                imp.setMerc2ID(ids[1]);
                imp.setMerc2Descricao("DESC " + ids[1]);
            case 1:
                imp.setMerc1ID(ids[0]);
                imp.setMerc1Descricao("DESC " + ids[0]);
        }
        return imp;
    }
    
    private void put(Map<MercadologicoKey, MercadologicoIMP> map, String... ids) {
        MercadologicoIMP imp = get(ids);
        MercadologicoKey key = MercadologicoKey.filterAndBuildKey(
                imp.getMerc1ID(),
                imp.getMerc2ID(),
                imp.getMerc3ID(),
                imp.getMerc4ID(),
                imp.getMerc5ID()
        );
        map.put(key, imp);
    }
    
}
