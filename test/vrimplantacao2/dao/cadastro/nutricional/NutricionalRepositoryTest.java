package vrimplantacao2.dao.cadastro.nutricional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
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
    }
    
    @Test
    public void testImportar_FILIZOLA() throws Exception {
        System.out.print("NutricionalRepositoryTest.testImportar_FILIZOLA()...");
        
        final List<NutricionalFilizolaVO> gravados = new ArrayList<>();
        
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        NutricionalFilizolaVO a = invocation.getArgumentAt(0, NutricionalFilizolaVO.class);
                        gravados.add(a);
                        return null;
                    }
                }
        ).when(provider).gravar(any(NutricionalFilizolaVO.class));        
        
        
        NutricionalFilizolaVO expected = NutricionalTestClasses.getFilizola1();
                
        List<NutricionalIMP> result = new ArrayList<>();
        result.add(NutricionalTestClasses.getImp1());
        
        NutricionalRepository repository = new NutricionalRepository(provider);
        
        repository.importar(result, new HashSet<>(Arrays.asList(OpcaoNutricional.FILIZOLA)));
        
        assertEquals(1, gravados.size());
        NutricionalTestClasses.compararFilizola(expected, gravados.get(0));
        
        System.out.println("OK");
    }
    
    @Test
    public void testConverterAnterior() {
        
        NutricionalAnteriorVO expected = NutricionalTestClasses.getAnterior1();
        NutricionalAnteriorVO actual = new NutricionalRepository(provider).converterAnterior(NutricionalTestClasses.getImp1());
        
        NutricionalTestClasses.compararAnterior(expected, actual);
        
        
        
    }
    
}
