package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoRepositoryClasses.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoVO;
import vrimplantacao2.vo.enums.SituacaoCreditoRotativo;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoItemIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class CreditoRotativoRepositoryTest {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CreditoRotativoProvider provider;
    
    public CreditoRotativoRepositoryTest() {
    }
    
    @Before
    public void init() throws Exception {
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getLojaVR()).thenReturn(1);
        when(provider.getLoja()).thenReturn("1");
        when(provider.getAnteriores()).thenReturn(new LinkedHashMap<String, CreditoRotativoAnteriorVO>());
        when(provider.getBaixasAnteriores()).thenReturn(new MultiMap<String, CreditoRotativoItemAnteriorVO>());
        
        MultiMap<String, CreditoRotativoItemAnteriorVO> todaBaixaAnterior = new MultiMap<>();
        {
            CreditoRotativoItemAnteriorVO antRotItem = getAnteriorItem1(true);
            todaBaixaAnterior.put(antRotItem, antRotItem.getSistema(), antRotItem.getLoja(), antRotItem.getId());
        }
        when(provider.getTodaBaixaAnterior()).thenReturn(todaBaixaAnterior);
        
        Map<String, CreditoRotativoAnteriorVO> listaAntRot = new HashMap<>();
        {
            CreditoRotativoAnteriorVO antRot = getAnterior1(true);
            listaAntRot.put(antRot.getId(), antRot);
            antRot = getAnterior2(true);
            listaAntRot.put(antRot.getId(), antRot);
        }
        when(provider.getTodoCreditoRotativoAnterior()).thenReturn(listaAntRot);
        
        Map<Integer, Double> baixas = new HashMap<>();
        {
            baixas.put(1, 2.5);
            baixas.put(2, 1.0);
        }
        when(provider.getBaixas()).thenReturn(baixas);
        
        
        MultiMap<String, ClientePreferencialAnteriorVO> clientes = new MultiMap<>();
        ClientePreferencialAnteriorVO ant = new ClientePreferencialAnteriorVO();
        ant.setSistema("TESTE");
        ant.setLoja("1");
        ant.setId("10");
        ant.setCnpj("12.639.458/0001-23");
        ant.setNome("TESTE CLIENTE");
        ClientePreferencialVO atual = new ClientePreferencialVO();
        atual.setId(11);
        atual.setNome("TESTE CLIENTE");
        atual.setCnpj(12639458000123L);
        ant.setCodigoAtual(atual);
        clientes.put(
                ant,
                ant.getSistema(),
                ant.getLoja(),
                ant.getId()
        );
        when(provider.getClientesAnteriores()).thenReturn(clientes);
    }

    @Test
    public void testImportarCreditoRotativo() throws Exception {
        final List<CreditoRotativoVO> rotGravados = new ArrayList<>();
        final List<CreditoRotativoAnteriorVO> rotAnterioresGravados = new ArrayList<>();
        final List<CreditoRotativoItemVO> rotItemGravados = new ArrayList<>();
        final List<CreditoRotativoItemAnteriorVO> rotItemAnterioresGravados = new ArrayList<>();
        
        doAnswer(
            new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    CreditoRotativoVO value = invocation.getArgument(0, CreditoRotativoVO.class);
                    value.setId(236);
                    rotGravados.add(value);
                    return null;
                }                
            }
        ).when(provider).gravarRotativo(any(CreditoRotativoVO.class));
        
        doAnswer(
            new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    CreditoRotativoAnteriorVO value = invocation.getArgument(0, CreditoRotativoAnteriorVO.class);
                    rotAnterioresGravados.add(value);
                    return null;
                }
            }
        ).when(provider).gravarRotativoAnterior(any(CreditoRotativoAnteriorVO.class));
        
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        CreditoRotativoItemVO value = invocation.getArgument(0, CreditoRotativoItemVO.class);
                        value.setId(569);
                        rotItemGravados.add(value);
                        return null;
                    }
                }
        ).when(provider).gravarRotativoItem(any(CreditoRotativoItemVO.class));
        
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        CreditoRotativoItemAnteriorVO value = invocation.getArgument(0, CreditoRotativoItemAnteriorVO.class);
                        rotItemAnterioresGravados.add(value);
                        return null;
                    }                    
                }
        ).when(provider).gravarRotativoItemAnterior(any(CreditoRotativoItemAnteriorVO.class));
        
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        
        rep.importarCreditoRotativo(getRotativo());
        
        assertEquals(1, rotGravados.size());
        assertEquals(1, rotAnterioresGravados.size());
        assertEquals(1, rotItemGravados.size());
        assertEquals(1, rotItemAnterioresGravados.size());
    }

    @Test
    public void testFiltrarRotativo() {
    }

    private List<CreditoRotativoIMP> getRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        result.add(getRotativo1());
        return result;
    }

    public CreditoRotativoIMP getRotativo1() throws Exception {
        CreditoRotativoIMP imp = new CreditoRotativoIMP();
        imp.setId("112");
        imp.setIdCliente("10");
        imp.setDataVencimento(DATE_FORMAT.parse("2017-05-20"));
        imp.setEcf("1A");
        imp.setDataEmissao(DATE_FORMAT.parse("2017-05-16"));
        imp.setJuros(5.20);
        imp.setMulta(2);
        imp.setNumeroCupom("1234");
        imp.setObservacao("TESTE de obs");
        imp.setParcela(1);
        imp.setValor(100.10);
        imp.addPagamento(
                "156",
                50.6,
                0,
                0,
                DATE_FORMAT.parse("2017-05-17"),
                "PAGAMENTO parcial"
        );
        return imp;
    }
    
    @Test
    public void testConverterRotativoAnterior() throws Exception {
        System.out.print("CreditoRotativoRepositoryTest.testConverterRotativoAnterior...");
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        CreditoRotativoAnteriorVO ant = rep.converterRotativoAnterior(getRotativo1());
        assertRotativo1Anterior(ant);
        System.out.println("OK");
    }
    
    @Test
    public void testConverterRotativo() throws Exception {
        System.out.print("CreditoRotativoRepositoryTest.testConverterRotativo...");
        
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        CreditoRotativoVO vo = rep.converterRotativo(getRotativo1());
        assertRotativo1(vo);
        
        System.out.println("OK");
    }
    
    @Test
    public void testConverterCreditoRotativoItem() throws Exception {
        System.out.print("CreditoRotativoRepositoryTest.testConverterCreditoRotativoItem...");
        
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        CreditoRotativoItemIMP parc1 = getRotativo1().getPagamentos().get(0);
        CreditoRotativoItemVO vo = rep.converterCreditoRotativoItem(parc1);        
        assertRotativo1Parc1(vo);
        
        System.out.println("OK");
    }
    
    @Test
    public void testConverterCreditoRotativoItemAnterior() throws Exception {
        System.out.print("CreditoRotativoRepositoryTest.testConverterCreditoRotativoItemAnterior...");
        
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        CreditoRotativoItemIMP parc1 = getRotativo1().getPagamentos().get(0);
        CreditoRotativoItemAnteriorVO vo = rep.converterCreditoRotativoItemAnterior(parc1);        
        assertRotativo1Parc1Ant(vo);
        
        System.out.println("OK");
    }

    public void assertRotativo1Anterior(CreditoRotativoAnteriorVO ant) throws ParseException {
        assertEquals("TESTE", ant.getSistema());
        assertEquals("1", ant.getLoja());
        assertEquals("112", ant.getId());
        assertEquals(DATE_FORMAT.parse("2017-05-20"), ant.getVencimento());
        assertEquals(100.10, ant.getValor(), 0.01);
        assertNull(ant.getCodigoAtual());
    }

    public void assertRotativo1(CreditoRotativoVO vo) throws Exception {
        assertEquals(0, vo.getId_clientePreferencial());
        assertEquals(DATE_FORMAT.parse("2017-05-16"), vo.getDataEmissao());
        assertEquals(DATE_FORMAT.parse("2017-05-20"), vo.getDataVencimento());
        assertEquals(1, vo.getEcf());
        assertEquals(5.2, vo.getValorJuros(), 0.01);
        assertEquals(2, vo.getValorMulta(), 0.01);
        assertEquals(1234, vo.getNumeroCupom());
        assertEquals("IMPORTADO VR TESTE DE OBS", vo.getObservacao());
        assertNull(vo.getDataExportacao());
        assertEquals(0, vo.getCpfDependente());
        assertEquals("", vo.getNomeDependente());
        assertEquals(1, vo.getId_loja());
        assertEquals(0, vo.getId_tipoLocalCobranca());
        assertEquals("", vo.getJustificativa());
        assertEquals(500001, vo.getMatricula());
        assertEquals(1, vo.getParcela());
        assertEquals(SituacaoCreditoRotativo.ABERTO, vo.getSituacaoCreditoRotativo());
        assertEquals(100.1, vo.getValor(), 0.01);
    }

    public void assertRotativo1Parc1(CreditoRotativoItemVO vo) throws Exception {
        assertEquals(DATE_FORMAT.parse("2017-05-17"), vo.getDataPagamento());
        assertEquals(DATE_FORMAT.parse("2017-05-17"), vo.getDatabaixa());
        assertEquals(1, vo.getId_loja());
        assertEquals("IMPORTADO VR PAGAMENTO PARCIAL", vo.getObservacao());
        assertEquals(50.6, vo.getValor(), 0.01);
        assertEquals(0, vo.getValorDesconto(), 0.01);
        assertEquals(0, vo.getValorMulta(), 0.01);
        assertEquals(50.6, vo.getValorTotal(), 0.01);
    }
    
    public void assertRotativo1Parc1Ant(CreditoRotativoItemAnteriorVO vo) throws Exception {
        assertEquals("TESTE", vo.getSistema());
        assertEquals("1", vo.getLoja());
        assertEquals("112", vo.getIdCreditoRotativo());
        assertEquals("156", vo.getId());
        assertEquals(50.6, vo.getValor(), 0.01);
        assertEquals(0, vo.getValorDesconto(), 0.01);
        assertEquals(0, vo.getValorMulta(), 0.01);
        assertEquals(DATE_FORMAT.parse("2017-05-17"), vo.getDataPagamento());
    }
    
    @Test
    public void testAgruparPagamentos() throws Exception {
        System.out.print("CreditoRotativoRepositoryTest.testAgruparPagamentos...");
        
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        
        List<CreditoRotativoPagamentoAgrupadoIMP> pags = new ArrayList<>();
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("ABC1", 10.65));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("ABC1", 5.12));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("2", 5.69));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("3A", 35.40));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("2", 8.97));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("ABC1", 2));
        
        Map<String, Double> agrupados = rep.agruparPagamentos(pags);
        
        assertEquals(3, agrupados.size());
        assertEquals(17.77, agrupados.get("ABC1"), 0.01);
        assertEquals(14.66, agrupados.get("2"), 0.01);
        assertEquals(35.40, agrupados.get("3A"), 0.01);
        
        System.out.println("OK");
    }
    
    @Test
    public void testSalvarPagamentosAgrupados() throws Exception {
        System.out.print("CreditoRotativoRepositoryTest.testSalvarPagamentosAgrupados...");
        
        final List<CreditoRotativoItemVO> itensGravados = new ArrayList<>();
        doAnswer(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        CreditoRotativoItemVO value = invocation.getArgument(0, CreditoRotativoItemVO.class);
                        itensGravados.add(value);
                        return null;
                    }
                }
        ).when(provider).gravarRotativoItem(any(CreditoRotativoItemVO.class));        
        
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        
        List<CreditoRotativoPagamentoAgrupadoIMP> pags = new ArrayList<>();
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("10", 10.65));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("10", 5.12));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("11", 5.69));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("12", 35.40));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("11", 8.97));
        pags.add(new CreditoRotativoPagamentoAgrupadoIMP("10", 2));
        
        rep.salvarPagamentosAgrupados(pags);
                
        System.out.println("OK");
    }
    
}
