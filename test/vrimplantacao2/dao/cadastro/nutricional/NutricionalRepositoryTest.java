package vrimplantacao2.dao.cadastro.nutricional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.nutricional.NutricionalAnteriorVO;
import vrimplantacao2.vo.importacao.NutricionalIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class NutricionalRepositoryTest {
    
    public NutricionalRepositoryTest() {
    }

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    public NutricionalRepositoryProvider provider;
    
    @Before
    public void init() throws Exception {
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getLoja()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);
        
        when(provider.getAnteriores()).thenReturn(new HashMap<String, NutricionalAnteriorVO>());
        
        setProdutos();
        
        MultiMap<Integer, Void> nutricionaisFilizola = new MultiMap<>();
        when(provider.getNutricionaisFilizola()).thenReturn(nutricionaisFilizola);
        MultiMap<Integer, Void> nutricionaisToledo = new MultiMap<>();
        when(provider.getNutricionaisToledo()).thenReturn(nutricionaisToledo);
    }

    public void setProdutos() throws Exception {
        Map<String, Integer> produtos = new HashMap<>();
        produtos.put("12", 1);
        produtos.put("29", 2);
        produtos.put("18", 3);
        when(provider.getProdutos()).thenReturn(produtos);
    }

    public void setAnteriores() throws Exception {
        Map<String, NutricionalAnteriorVO> anteriores = new HashMap();
        anteriores.put("1", NutricionalTestClasses.getAnterior1());
        when(provider.getAnteriores()).thenReturn(anteriores);
    }
    
    @Test
    public void testImportar_FILIZOLA() throws Exception {
        System.out.print("NutricionalRepositoryTest.testImportar_FILIZOLA()...");
        
        final List<NutricionalFilizolaVO> gravados = new ArrayList<>();
        final List<NutricionalAnteriorVO> anteriores = new ArrayList<>();
        
        doAnswer(
                new Answer() {
                    int id = 0;
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        NutricionalFilizolaVO a = invocation.getArgument(0, NutricionalFilizolaVO.class);
                        a.setId(++id);
                        gravados.add(a);
                        return null;
                    }
                }
        ).when(provider).gravar(any(NutricionalFilizolaVO.class));
        
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        NutricionalAnteriorVO a = invocation.getArgument(0, NutricionalAnteriorVO.class);
                        anteriores.add(a);
                        return null;
                    }
                }
        ).when(provider).gravar(any(NutricionalAnteriorVO.class));
        
        final MultiMap<Integer, Void> itens = new MultiMap<>();
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Integer idNutricional = invocation.getArgument(0, Integer.class);
                        Integer idProduto = invocation.getArgument(1, Integer.class);
                        
                        itens.put(null, idNutricional, idProduto);
                        return null;
                    }
                }
        ).when(provider).gravarItemFilizola(anyInt(), anyInt());
        
        NutricionalFilizolaVO expectedFilizola = NutricionalTestClasses.getFilizola1();
        NutricionalAnteriorVO expectedAnterior = NutricionalTestClasses.getAnterior1();
        expectedAnterior.setCodigoAtualFilizola(1);
                
        List<NutricionalIMP> result = new ArrayList<>();
        result.add(NutricionalTestClasses.getImp1());
        
        NutricionalRepository repository = new NutricionalRepository(provider);
        
        repository.importar(result, new HashSet<>(Arrays.asList(OpcaoNutricional.FILIZOLA)));
        
        assertEquals(1, gravados.size());
        NutricionalTestClasses.compararFilizola(expectedFilizola, gravados.get(0));
        
        assertEquals(1, anteriores.size());
        NutricionalTestClasses.compararAnterior(expectedAnterior, anteriores.get(0));
        
        assertEquals(3, itens.size());
        assertTrue(itens.containsKey(1, 1));
        assertTrue(itens.containsKey(1, 2));
        assertTrue(itens.containsKey(1, 3));
        
        System.out.println("OK");
    }
    
    @Test
    public void testImportar_TOLEDO() throws Exception {
        System.out.print("NutricionalRepositoryTest.testImportar_TOLEDO()...");
        
        final List<NutricionalToledoVO> gravados = new ArrayList<>();
        final List<NutricionalAnteriorVO> anteriores = new ArrayList<>();
        
        doAnswer(
                new Answer() {
                    int id = 0;
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        NutricionalToledoVO a = invocation.getArgument(0, NutricionalToledoVO.class);
                        a.setId(++id);
                        gravados.add(a);
                        return null;
                    }
                }
        ).when(provider).gravar(any(NutricionalToledoVO.class));
        
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        NutricionalAnteriorVO a = invocation.getArgument(0, NutricionalAnteriorVO.class);
                        anteriores.add(a);
                        return null;
                    }
                }
        ).when(provider).gravar(any(NutricionalAnteriorVO.class));
        
        final MultiMap<Integer, Void> itens = new MultiMap<>();
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Integer idNutricional = invocation.getArgument(0, Integer.class);
                        Integer idProduto = invocation.getArgument(1, Integer.class);
                        
                        itens.put(null, idNutricional, idProduto);
                        return null;
                    }
                }
        ).when(provider).gravarItemToledo(anyInt(), anyInt());
        
        NutricionalToledoVO expectedToledo = NutricionalTestClasses.getToledo1();
        NutricionalAnteriorVO expectedAnterior = NutricionalTestClasses.getAnterior1();
        expectedAnterior.setCodigoAtualToledo(1);
                
        List<NutricionalIMP> result = new ArrayList<>();
        result.add(NutricionalTestClasses.getImp1());
        
        NutricionalRepository repository = new NutricionalRepository(provider);
        
        repository.importar(result, new HashSet<>(Arrays.asList(OpcaoNutricional.TOLEDO)));
        
        assertEquals(1, gravados.size());
        NutricionalTestClasses.compararToledo(expectedToledo, gravados.get(0));
        
        assertEquals(1, anteriores.size());
        NutricionalTestClasses.compararAnterior(expectedAnterior, anteriores.get(0));
        
        assertEquals(3, itens.size());
        assertTrue(itens.containsKey(1, 1));
        assertTrue(itens.containsKey(1, 2));
        assertTrue(itens.containsKey(1, 3));
        
        System.out.println("OK");
    }
    
    @Test
    public void testConverterAnterior() {
        
        NutricionalAnteriorVO expected = NutricionalTestClasses.getAnterior1();
        NutricionalAnteriorVO actual = new NutricionalRepository(provider).converterAnterior(NutricionalTestClasses.getImp1());
        
        NutricionalTestClasses.compararAnterior(expected, actual);
        
    }
    
    @Test
    public void converterNutricionalFilizola() {
        
        NutricionalFilizolaVO expected = NutricionalTestClasses.getFilizola1();
        expected.setId(0);
        NutricionalFilizolaVO actual = new NutricionalRepository(provider).converterNutricionalFilizola(NutricionalTestClasses.getImp1());
        
        NutricionalTestClasses.compararFilizola(expected, actual);
        
    }
    
    @Test
    public void converterNutricionalToledo() {
        
        NutricionalToledoVO expected = NutricionalTestClasses.getToledo1();
        expected.setId(0);
        NutricionalToledoVO actual = new NutricionalRepository(provider).converterNutricionalToledo(NutricionalTestClasses.getImp1());
        
        NutricionalTestClasses.compararToledo(expected, actual);
        
    }
    
}
