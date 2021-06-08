package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import static vrimplantacao2.dao.cadastro.fiscal.pautafiscal.PautaFiscalRepositoryClasses.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class PautaFiscalRepositoryTest {
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PautaFiscalRepositoryProvider provider;
    
    public PautaFiscalRepositoryTest() {
    }
    
    @Before
    public void init() throws Exception {
        Map<String, PautaFiscalAnteriorVO> anteriores = new LinkedHashMap<>();
        
        PautaFiscalAnteriorVO ant = getAnt1(true);
        anteriores.put(ant.getId(), ant);
        ant = getAnt2(false);
        anteriores.put(ant.getId(), ant);
        
        when(provider.getAnteriores()).thenReturn(anteriores);
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getLoja()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);
        when(provider.getAliquota(eq(0), eq(7d), eq(0d))).thenReturn(new Icms(0, "7%", 0, 7, 0));
        when(provider.getAliquota(eq(0), eq(12d), eq(0d))).thenReturn(new Icms(1, "12%", 0, 12, 0));
        when(provider.getAliquota(eq(0), eq(25d), eq(0d))).thenReturn(new Icms(3, "25%", 0, 25, 0));
        when(provider.getUfPadrao()).thenReturn(new EstadoVO(35, "SP", "SAO PAULO"));
        Map<String, EstadoVO> ufs = new LinkedHashMap<>();
        ufs.put("SP", new EstadoVO(35, "SP", "SAO PAULO"));
        when(provider.getEstados()).thenReturn(ufs);
        
        NcmVO ncm = new NcmVO();
        ncm.setId(1);
        ncm.setDescricao("TESTE");
        ncm.setNcm1(201);
        ncm.setNcm2(20);
        ncm.setNcm3(10);
        ncm.setNivel(3);
        when(provider.getNcm(eq("0201.20.10"))).thenReturn(ncm);
        
        ncm = new NcmVO();
        ncm.setId(2);
        ncm.setDescricao("TESTE2");
        ncm.setNcm1(202);
        ncm.setNcm2(30);
        ncm.setNcm3(0);
        ncm.setNivel(3);
        when(provider.getNcm(eq("0202.30.00"))).thenReturn(ncm);
    }

    @Test
    public void testImportar() throws Exception {
        System.out.print("PautaFiscalRepositoryTest.testImportar()...");
        
        final List<PautaFiscalVO> gravados = new ArrayList<>();
        doAnswer(
            new Answer() {
                int ids = 0;
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    PautaFiscalVO pauta = invocation.getArgumentAt(0, PautaFiscalVO.class);
                    pauta.setId(++ids);
                    gravados.add(pauta);
                    return null;
                }
            }
        ).when(provider).gravar(any(PautaFiscalVO.class), any(Set.class));
        
        final List<PautaFiscalVO> atualizar = new ArrayList<>();
        doAnswer(
            new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    PautaFiscalVO pauta = invocation.getArgumentAt(0, PautaFiscalVO.class);
                    atualizar.add(pauta);
                    return null;
                }
            }
        ).when(provider).atualizar(any(PautaFiscalVO.class), any(Set.class));
        
        final List<PautaFiscalAnteriorVO> gravadosAnterior = new ArrayList<>();
        doAnswer(
            new Answer() {
                int ids = 0;
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    PautaFiscalAnteriorVO pauta = invocation.getArgumentAt(0, PautaFiscalAnteriorVO.class);
                    gravadosAnterior.add(pauta);
                    return null;
                }
            }
        ).when(provider).gravarAnterior(any(PautaFiscalAnteriorVO.class));
        
        final List<PautaFiscalAnteriorVO> atualizarAnterior = new ArrayList<>();
        doAnswer(
            new Answer() {
                int ids = 0;
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    PautaFiscalAnteriorVO pauta = invocation.getArgumentAt(0, PautaFiscalAnteriorVO.class);
                    atualizarAnterior.add(pauta);
                    return null;
                }
            }
        ).when(provider).atualizar(any(PautaFiscalAnteriorVO.class));
        
        List<PautaFiscalIMP> imps = new ArrayList<>();
        
        imps.add(getImp1());
        imps.add(getImp3());
        imps.add(getImp2());
        imps.add(getImp2());
        
        PautaFiscalRepository rep = new PautaFiscalRepository(provider);
        
        Parametros.setFactory(new Parametros.Factory() {
            @Override
            public Parametros newInstance() {
                return mock(Parametros.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
            }
        });        
        
        rep.importar(imps, EnumSet.of(OpcaoFiscal.NOVOS));
        
        assertEquals(2, gravados.size());
        assertVO(getPauta3(), gravados.get(0));
        assertVO(getPauta2(), gravados.get(1));
        
        assertEquals(1, atualizar.size());
        assertVO(getPauta1(), atualizar.get(0));
        
        assertEquals(1, gravadosAnterior.size());
        assertAnterior(getAnt3(true), gravadosAnterior.get(0));
        
        assertEquals(2, atualizarAnterior.size());
        assertAnterior(getAnt1(true), atualizarAnterior.get(0));
        assertAnterior(getAnt2(true), atualizarAnterior.get(1));
        
        System.out.println("OK");
    }

    @Test
    public void testOrganizar() throws Exception {
        System.out.print("PautaFiscalRepositoryTest.testOrganizar()...");
        
        List<PautaFiscalIMP> imps = new ArrayList<>();
        
        imps.add(getImp1());
        imps.add(getImp3());
        imps.add(getImp2());
        imps.add(getImp2());
        
        PautaFiscalRepository rep = new PautaFiscalRepository(provider);
        Map<String, PautaFiscalIMP> organizados = rep.organizar(imps);
        
        assertEquals(3, organizados.size());
        
        assertImp(getImp1(), organizados.get("asd1234"));
        assertImp(getImp2(), organizados.get("789"));
        assertImp(getImp3(), organizados.get("123"));
                
        System.out.println("OK");
    }

    @Test
    public void testConverterPauta() throws Exception {
        System.out.print("PautaFiscalRepositoryTest.testConverterPauta()...");
        
        PautaFiscalIMP imp = getImp1();
        
        PautaFiscalRepository rep = new PautaFiscalRepository(provider);
        NcmVO ncm = new NcmVO();
        ncm.setNcm1(201);
        ncm.setNcm2(20);
        ncm.setNcm3(10);
        PautaFiscalVO actual = rep.converterPauta(imp, ncm);
        
        assertVO(getPauta1(), actual);
        
        System.out.println("OK");        
    }

    @Test
    public void testConverterPautaAnterior() throws Exception {
        System.out.print("PautaFiscalRepositoryTest.testConverterPautaAnterior()...");
        
        PautaFiscalIMP imp = getImp1();
        
        PautaFiscalRepository rep = new PautaFiscalRepository(provider);
        PautaFiscalAnteriorVO actual = rep.converterPautaAnterior(imp);
        
        assertAnterior(getAnt1(false), actual);
        
        System.out.println("OK");   
    }
    
}
