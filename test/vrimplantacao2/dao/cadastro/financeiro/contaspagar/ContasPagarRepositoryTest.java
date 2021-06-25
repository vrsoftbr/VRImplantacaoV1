package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContasPagarTestClasses.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorTestClasses;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.importacao.ContaPagarIMP;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class ContasPagarRepositoryTest {
    
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    private static final String SISTEMA = "TESTE";
    private static final String AGRUPADOR = "1";
        
    @Mock
    public ContasPagarProvider provider;
    
    public ContasPagarRepositoryTest() {
    }
    
    private List<ContaPagarIMP> getTestList() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
  
        result.add(getImp1());
        result.add(getImp2());
        result.add(getImp1());
        result.add(getImp3());
        
        return result;
    }

    private MultiMap<String, Void> getVencimentos() {
        MultiMap<String, Void> result = new MultiMap<>();
        
        return result;
    }

    private MultiMap<String, FornecedorAnteriorVO> getFornecedorList() throws Exception {
        MultiMap<String, FornecedorAnteriorVO> result = new MultiMap<>();
        
        FornecedorAnteriorVO ant = FornecedorTestClasses.getAnterior1();
        ant.setImportSistema(SISTEMA);
        ant.setImportLoja(AGRUPADOR);
        result.put(ant, SISTEMA, AGRUPADOR, ant.getImportId());
        
        ant = FornecedorTestClasses.getAnterior2();
        ant.setImportSistema(SISTEMA);
        ant.setImportLoja(AGRUPADOR);
        result.put(ant, SISTEMA, AGRUPADOR, ant.getImportId());
        
        ant = FornecedorTestClasses.getAnterior3();
        ant.setImportSistema(SISTEMA);
        ant.setImportLoja(AGRUPADOR);
        result.put(ant, SISTEMA, AGRUPADOR, ant.getImportId());
        
        return result;
    }

    private MultiMap<String, ContaPagarAnteriorVO> getAnteriorList() throws Exception {
        MultiMap<String, ContaPagarAnteriorVO> result = new MultiMap<>();
        
        ContaPagarAnteriorVO ant = getAnterior1(false);
        ant.setSistema(SISTEMA);
        ant.setAgrupador(AGRUPADOR);
        result.put(ant, SISTEMA, AGRUPADOR, ant.getId());
        
        return result;
    }    
    
    private MultiMap<String, FornecedorAnteriorVO> fornecedorList;
    private MultiMap<String, ContaPagarAnteriorVO> anteriorList;
    private MultiMap<String, Void> vencimentos;
    
    @Before
    public void init() throws Exception {
        FornecedorVO.testing = true;
        fornecedorList = getFornecedorList();
        anteriorList = getAnteriorList();
        vencimentos = getVencimentos();
        
        when(provider.getSistema()).thenReturn("TESTE");
        when(provider.getAgrupador()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);
        when(provider.getFornecedores()).thenReturn(fornecedorList);
        when(provider.getAnteriores()).thenReturn(anteriorList);
        when(provider.getPagamentos(eq(true))).thenReturn(vencimentos);
    }

    @Test
    public void testConverterEmOutrasDispesas() throws Exception {
        System.out.print("ContasPagarRepositoryTest.testConverterEmOutrasDispesas()...");
        ContasPagarRepository rep = new ContasPagarRepository(provider);
        PagarOutrasDespesasVO expected = getOutraDespesa1();
        PagarOutrasDespesasVO actual = rep.converterEmOutrasDispesas(getImp1());
        
        assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
        assertEquals(expected.getDataEntrada(), actual.getDataEntrada());
        assertEquals(expected.getDataHoraAlteracao(), actual.getDataHoraAlteracao());
        assertEquals(expected.getId_loja(), actual.getId_loja());
        assertEquals(expected.getId_tipopiscofins(), actual.getId_tipopiscofins());
        assertEquals(expected.getNumeroDocumento(), actual.getNumeroDocumento());
        assertEquals(expected.getObservacao(), actual.getObservacao());
        assertEquals(expected.getSituacaoPagarOutrasDespesas(), actual.getSituacaoPagarOutrasDespesas());
        assertEquals(expected.getIdTipoEntrada(), actual.getIdTipoEntrada());
        assertEquals(expected.getValor(), actual.getValor(), 0.01D);

        System.out.println("OK");
    }
    
    @Test
    public void testConverterAnterior() throws Exception {
        System.out.print("ContasPagarRepositoryTest.testConverterAnterior()...");
        ContasPagarRepository rep = new ContasPagarRepository(provider);
        ContaPagarAnteriorVO expected = getAnterior1(true);
        ContaPagarAnteriorVO atual = rep.converterAnterior(getImp1());
        
        assertEquals(expected.getAgrupador(), atual.getAgrupador());
        assertEquals(expected.getCodigoAtual(), atual.getCodigoAtual());
        assertEquals(expected.getDataEmissao(), atual.getDataEmissao());
        assertEquals(expected.getDocumento(), atual.getDocumento());
        assertEquals(expected.getId(), atual.getId());
        assertEquals(expected.getId_fornecedor(), atual.getId_fornecedor());
        assertEquals(expected.getSistema(), atual.getSistema());
        assertEquals(expected.getValor(), atual.getValor(), 0.01D);   
        System.out.println("OK");
    }
    
    @Test
    public void testeAtualizarConta() throws Exception {
        System.out.print("ContasPagarRepositoryTest.testeAtualizarConta()...");
        doAnswer(new Answer<Void> () {
            int ids = 1;
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PagarOutrasDespesasVO gravar = invocation.getArgument(0, PagarOutrasDespesasVO.class);
                gravar.setId(ids);
                ids++;
                return null;
            }
        }).when(provider).gravar(any(PagarOutrasDespesasVO.class));
        ContasPagarRepository rep = new ContasPagarRepository(provider);
        {
            PagarOutrasDespesasVO expected = ContasPagarTestClasses.getOutraDespesa1();
            
            PagarOutrasDespesasVO atual = rep.atualizarConta(1, getImp1(), expected.getIdFornecedor(), new HashSet<>(Arrays.asList(new OpcaoContaPagar[]{})));

            assertEquals(1, atual.getId());
            assertEquals(expected.getDataEmissao(), atual.getDataEmissao());
            assertEquals(expected.getDataEntrada(), atual.getDataEntrada());
            assertEquals(expected.getDataHoraAlteracao(), atual.getDataHoraAlteracao());
            assertEquals(expected.getIdFornecedor(), atual.getIdFornecedor());
            assertEquals(expected.getId_loja(), atual.getId_loja());
            assertEquals(expected.getId_tipopiscofins(), atual.getId_tipopiscofins());
            assertEquals(expected.getNumeroDocumento(), atual.getNumeroDocumento());
            assertEquals(expected.getObservacao(), atual.getObservacao());
            assertEquals(expected.getSituacaoPagarOutrasDespesas(), atual.getSituacaoPagarOutrasDespesas());
            assertEquals(expected.getIdTipoEntrada(), atual.getIdTipoEntrada());
            assertEquals(expected.getValor(), atual.getValor(), 0.01D); 

        }
        {
            PagarOutrasDespesasVO expected = ContasPagarTestClasses.getOutraDespesa2();
            
            PagarOutrasDespesasVO atual = rep.atualizarConta(2, getImp2(), expected.getIdFornecedor(), new HashSet<>(Arrays.asList(new OpcaoContaPagar[]{})));

            assertEquals(2, atual.getId());
            assertEquals(expected.getDataEmissao(), atual.getDataEmissao());
            assertEquals(expected.getDataEntrada(), atual.getDataEntrada());
            assertEquals(expected.getDataHoraAlteracao(), atual.getDataHoraAlteracao());
            assertEquals(expected.getIdFornecedor(), atual.getIdFornecedor());
            assertEquals(expected.getId_loja(), atual.getId_loja());
            assertEquals(expected.getId_tipopiscofins(), atual.getId_tipopiscofins());
            assertEquals(expected.getNumeroDocumento(), atual.getNumeroDocumento());
            assertEquals(expected.getObservacao(), atual.getObservacao());
            assertEquals(expected.getSituacaoPagarOutrasDespesas(), atual.getSituacaoPagarOutrasDespesas());
            assertEquals(expected.getIdTipoEntrada(), atual.getIdTipoEntrada());
            assertEquals(expected.getValor(), atual.getValor(), 0.01D);
            
        }
        System.out.println("OK");
    }
    
    @Test
    /**
     * Teste que inclui duas novas contas e uma parcela para uma já existente.
     */
    public void testSalvar() throws Exception {
        System.out.print("ContasPagarRepositoryTest.testSalvar()...");
        
        final List<PagarOutrasDespesasVO> db = new ArrayList<>();
        doAnswer(new Answer<Void> () {
            int ids = 1;
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PagarOutrasDespesasVO gravar = invocation.getArgument(0, PagarOutrasDespesasVO.class);
                gravar.setId(ids);
                db.add(gravar);
                ids++;
                return null;
            }
        }).when(provider).gravar(any(PagarOutrasDespesasVO.class));        
        
        final List<PagarOutrasDespesasVencimentoVO> dbVenc = new ArrayList<>();
        doAnswer(new Answer<Void> () {
            int ids = 1;
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PagarOutrasDespesasVencimentoVO gravar = invocation.getArgument(0, PagarOutrasDespesasVencimentoVO.class);
                gravar.setId(ids);
                ids++;
                dbVenc.add(gravar);
                return null;
            }
        }).when(provider).gravarVencimento(any(PagarOutrasDespesasVencimentoVO.class));
        
        
        ContasPagarRepository rep = new ContasPagarRepository(provider) {};
        
        rep.salvar(getTestList(), OpcaoContaPagar.NOVOS, OpcaoContaPagar.IMPORTAR_OUTRASDESPESAS);
        
        assertEquals(2, db.size());
        assertEquals(3, dbVenc.size());
        
        {
            PagarOutrasDespesasVO expected = ContasPagarTestClasses.getOutraDespesa2();
            PagarOutrasDespesasVO actual = db.get(0);
            
            assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
            assertEquals(expected.getDataEntrada(), actual.getDataEntrada());
            assertEquals(expected.getDataHoraAlteracao(), actual.getDataHoraAlteracao());
            assertEquals(expected.getId_loja(), actual.getId_loja());
            assertEquals(expected.getId_tipopiscofins(), actual.getId_tipopiscofins());
            assertEquals(expected.getNumeroDocumento(), actual.getNumeroDocumento());
            assertEquals(expected.getObservacao(), actual.getObservacao());
            assertEquals(expected.getSituacaoPagarOutrasDespesas(), actual.getSituacaoPagarOutrasDespesas());
            assertEquals(expected.getIdTipoEntrada(), actual.getIdTipoEntrada());
            assertEquals(expected.getValor(), actual.getValor(), 0.01D);        
        }
        
        {
            PagarOutrasDespesasVO expected = ContasPagarTestClasses.getOutraDespesa3();
            PagarOutrasDespesasVO actual = db.get(1);
            
            assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
            assertEquals(expected.getDataEntrada(), actual.getDataEntrada());
            assertEquals(expected.getDataHoraAlteracao(), actual.getDataHoraAlteracao());
            assertEquals(expected.getId_loja(), actual.getId_loja());
            assertEquals(expected.getId_tipopiscofins(), actual.getId_tipopiscofins());
            assertEquals(expected.getNumeroDocumento(), actual.getNumeroDocumento());
            assertEquals(expected.getObservacao(), actual.getObservacao());
            assertEquals(expected.getSituacaoPagarOutrasDespesas(), actual.getSituacaoPagarOutrasDespesas());
            assertEquals(expected.getIdTipoEntrada(), actual.getIdTipoEntrada());
            assertEquals(expected.getValor(), actual.getValor(), 0.01D);        
        }
        
        {            
            PagarOutrasDespesasVencimentoVO actual = dbVenc.get(0);
            assertEquals(format.parse("10/01/2017"), actual.getDataVencimento());
            assertEquals(60.0, actual.getValor(), 0.01D);
        }
        {            
            PagarOutrasDespesasVencimentoVO actual = dbVenc.get(1);
            assertEquals(format.parse("10/02/2017"), actual.getDataVencimento());
            assertEquals(60.0, actual.getValor(), 0.01D);
        }
        {            
            PagarOutrasDespesasVencimentoVO actual = dbVenc.get(2);
            assertEquals(format.parse("10/01/2017"), actual.getDataVencimento());
            assertEquals(140.65, actual.getValor(), 0.01D);
        }   
        
        System.out.println("OK");
    }

    @Test
    public void testSalvarDuplicado() throws Exception {
        System.out.print("ContasPagarRepositoryTest.testSalvarDuplicado()...");                
        
        final List<PagarOutrasDespesasVO> db = new ArrayList<>();
        doAnswer(new Answer<Void> () {
            int ids = 1;
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PagarOutrasDespesasVO gravar = invocation.getArgument(0, PagarOutrasDespesasVO.class);
                gravar.setId(ids);
                db.add(gravar);
                ids++;
                return null;
            }
        }).when(provider).gravar(any(PagarOutrasDespesasVO.class));        
        
        final List<PagarOutrasDespesasVencimentoVO> dbVenc = new ArrayList<>();
        doAnswer(new Answer<Void> () {
            int ids = 1;
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PagarOutrasDespesasVencimentoVO gravar = invocation.getArgument(0, PagarOutrasDespesasVencimentoVO.class);
                gravar.setId(ids);
                ids++;
                dbVenc.add(gravar);
                return null;
            }
        }).when(provider).gravarVencimento(any(PagarOutrasDespesasVencimentoVO.class));
        
        
        ContasPagarRepository rep = new ContasPagarRepository(provider) {};
        
        rep.salvar(getTestList(), OpcaoContaPagar.NOVOS, OpcaoContaPagar.IMPORTAR_OUTRASDESPESAS);
        rep.salvar(getTestList(), OpcaoContaPagar.NOVOS, OpcaoContaPagar.IMPORTAR_OUTRASDESPESAS);
        
        assertEquals(2, db.size());
        assertEquals(3, dbVenc.size());
        
        {
            PagarOutrasDespesasVO expected = ContasPagarTestClasses.getOutraDespesa2();
            PagarOutrasDespesasVO actual = db.get(0);
            
            assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
            assertEquals(expected.getDataEntrada(), actual.getDataEntrada());
            assertEquals(expected.getDataHoraAlteracao(), actual.getDataHoraAlteracao());
            assertEquals(expected.getId_loja(), actual.getId_loja());
            assertEquals(expected.getId_tipopiscofins(), actual.getId_tipopiscofins());
            assertEquals(expected.getNumeroDocumento(), actual.getNumeroDocumento());
            assertEquals(expected.getObservacao(), actual.getObservacao());
            assertEquals(expected.getSituacaoPagarOutrasDespesas(), actual.getSituacaoPagarOutrasDespesas());
            assertEquals(expected.getIdTipoEntrada(), actual.getIdTipoEntrada());
            assertEquals(expected.getValor(), actual.getValor(), 0.01D);        
        }
        
        {
            PagarOutrasDespesasVO expected = ContasPagarTestClasses.getOutraDespesa3();
            PagarOutrasDespesasVO actual = db.get(1);
            
            assertEquals(expected.getDataEmissao(), actual.getDataEmissao());
            assertEquals(expected.getDataEntrada(), actual.getDataEntrada());
            assertEquals(expected.getDataHoraAlteracao(), actual.getDataHoraAlteracao());
            assertEquals(expected.getId_loja(), actual.getId_loja());
            assertEquals(expected.getId_tipopiscofins(), actual.getId_tipopiscofins());
            assertEquals(expected.getNumeroDocumento(), actual.getNumeroDocumento());
            assertEquals(expected.getObservacao(), actual.getObservacao());
            assertEquals(expected.getSituacaoPagarOutrasDespesas(), actual.getSituacaoPagarOutrasDespesas());
            assertEquals(expected.getIdTipoEntrada(), actual.getIdTipoEntrada());
            assertEquals(expected.getValor(), actual.getValor(), 0.01D);        
        }
        
        {            
            PagarOutrasDespesasVencimentoVO actual = dbVenc.get(0);
            assertEquals(format.parse("10/01/2017"), actual.getDataVencimento());
            assertEquals(60.0, actual.getValor(), 0.01D);
        }
        {            
            PagarOutrasDespesasVencimentoVO actual = dbVenc.get(1);
            assertEquals(format.parse("10/02/2017"), actual.getDataVencimento());
            assertEquals(60.0, actual.getValor(), 0.01D);
        }
        {            
            PagarOutrasDespesasVencimentoVO actual = dbVenc.get(2);
            assertEquals(format.parse("10/01/2017"), actual.getDataVencimento());
            assertEquals(140.65, actual.getValor(), 0.01D);
        }   
        
        System.out.println("OK");
    }
    
}
