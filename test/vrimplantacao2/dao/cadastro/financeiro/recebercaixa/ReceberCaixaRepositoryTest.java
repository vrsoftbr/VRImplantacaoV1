package vrimplantacao2.dao.cadastro.financeiro.recebercaixa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static vrimplantacao2.dao.cadastro.financeiro.recebercaixa.ReceberCaixaRepositoryClasses.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.recebimentocaixa.RecebimentoCaixaVO;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class ReceberCaixaRepositoryTest {
    
    @Mock
    public ReceberCaixaRepositoryProvider provider;
    
    public ReceberCaixaRepositoryTest() {
    }

    @Before
    public void init() throws Exception {
        Map<String, Integer> mapaRecebiveis = new LinkedHashMap<>();
        
        mapaRecebiveis.put("10", 1);
        mapaRecebiveis.put("dfg0", 2);
        mapaRecebiveis.put("134", 3);
        mapaRecebiveis.put("789", 4);
        
        Map<String, RecebimentoCaixaAnteriorVO> anteriores = new LinkedHashMap<>();
        RecebimentoCaixaAnteriorVO rec = getRecebimentoAnterior4();
        rec.setCodigoAtual(getRecebimento4());
        anteriores.put(rec.getId(), rec);
        rec = getRecebimentoAnterior3();
        anteriores.put(rec.getId(), rec);
        
        
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getAgrupador()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);
        when(provider.getMapaTipoRecebivel()).thenReturn(mapaRecebiveis);
        when(provider.getAnteriores()).thenReturn(anteriores);
    }
    
    @Test
    public void testSalvar() throws Exception {
        System.out.print("ReceberCaixaRepositoryTest.testSalvar()...");
        ReceberCaixaRepository rep = new ReceberCaixaRepository(provider);
        List<RecebimentoCaixaIMP> normal = new ArrayList<>();
        
        normal.add(getImp1());
        normal.add(getImp2());
        normal.add(getImp2());
        normal.add(getImp3());
        normal.add(getImp4());
        
        final List<RecebimentoCaixaVO> novos = new ArrayList<>();
        final List<RecebimentoCaixaVO> atualizados = new ArrayList<>();
        doAnswer(
            new Answer<Void> () {
                int ids = 0;
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    RecebimentoCaixaVO recebimento = invocation.getArgumentAt(0, RecebimentoCaixaVO.class);
                    ids++;
                    recebimento.setId(ids);
                    novos.add(recebimento);
                    return null;
                }
            }
        ).when(provider).gravarRecebimentoCaixa(any(RecebimentoCaixaVO.class));
        
        Set<OpcaoRecebimentoCaixa> opt = new HashSet<>();
        opt.add(OpcaoRecebimentoCaixa.NOVOS);
        doAnswer(
            new Answer<Void> () {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    RecebimentoCaixaVO recebimento = invocation.getArgumentAt(0, RecebimentoCaixaVO.class);
                    atualizados.add(recebimento);
                    return null;
                }
            }
        ).when(provider).atualizarRecebimentoCaixa(any(RecebimentoCaixaVO.class), eq(opt));
        
        rep.salvar(normal, OpcaoRecebimentoCaixa.NOVOS);
        
        assertEquals(3, novos.size());
        
        RecebimentoCaixaVO vo = novos.get(0);
        assertEquals(1, vo.getId());
        assertEquals(1, vo.getIdTipoRecebivel());
        assertRecebimento(getRecebimento1(), vo);
        
        vo = novos.get(1);
        assertEquals(2, vo.getId());
        assertEquals(2, vo.getIdTipoRecebivel());
        assertRecebimento(getRecebimento2(), vo);
        
        vo = novos.get(2);
        assertEquals(3, vo.getId());
        assertEquals(3, vo.getIdTipoRecebivel());
        assertRecebimento(getRecebimento3(), vo);
        
        vo = atualizados.get(0);
        assertEquals(4, vo.getId());
        assertEquals(4, vo.getIdTipoRecebivel());
        assertRecebimento(getRecebimento4(), vo);
        
        System.out.println("OK");
    }

    @Test
    public void testOrganizar() throws Exception {
        System.out.print("ReceberCaixaRepositoryTest.testOrganizar()...");
        ReceberCaixaRepository rep = new ReceberCaixaRepository(provider);
        List<RecebimentoCaixaIMP> normal = new ArrayList<>();
        
        normal.add(getImp1());
        normal.add(getImp2());
        normal.add(getImp2());
        normal.add(getImp3());
        
        List<RecebimentoCaixaIMP> convert = new ArrayList<>(rep.organizar(normal).values());
        
        assertEquals(3, convert.size());
        assertImp(getImp1(), convert.get(0));
        assertImp(getImp2(), convert.get(1));
        assertImp(getImp3(), convert.get(2));
        System.out.println("OK");
    }

    @Test
    public void testConverterRecebimento() throws Exception {
        System.out.print("ReceberCaixaRepositoryTest.testConverterRecebimento()...");
        ReceberCaixaRepository rep = new ReceberCaixaRepository(provider);
        
        RecebimentoCaixaIMP expected = getImp1();
        RecebimentoCaixaVO actual = rep.converterRecebimento(expected);        
        assertRecebimento(getRecebimento1(), actual);
        
        expected = getImp2();
        actual = rep.converterRecebimento(expected);        
        assertRecebimento(getRecebimento2(), actual);
        
        expected = getImp3();
        actual = rep.converterRecebimento(expected);        
        assertRecebimento(getRecebimento3(), actual);
        System.out.println("OK");
    }

    @Test
    public void testConverterRecebimentoAnterior() throws Exception {
        System.out.print("ReceberCaixaRepositoryTest.testConverterRecebimentoAnterior()...");
        ReceberCaixaRepository rep = new ReceberCaixaRepository(provider);
        
        RecebimentoCaixaIMP expected = getImp1();
        RecebimentoCaixaAnteriorVO actual = rep.converterRecebimentoAnterior(expected);        
        assertRecebimentoAnterior(getRecebimentoAnterior1(), actual);
        
        expected = getImp2();
        actual = rep.converterRecebimentoAnterior(expected);        
        assertRecebimentoAnterior(getRecebimentoAnterior2(), actual);
        
        expected = getImp3();
        actual = rep.converterRecebimentoAnterior(expected);        
        assertRecebimentoAnterior(getRecebimentoAnterior3(), actual);
        System.out.println("OK");
    }
    
}
