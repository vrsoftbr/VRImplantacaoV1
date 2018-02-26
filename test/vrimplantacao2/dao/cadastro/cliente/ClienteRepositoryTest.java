package vrimplantacao2.dao.cadastro.cliente;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.mockito.Mock;
import vrimplantacao2.vo.importacao.ClienteIMP;
import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;

/**
 *
 * @author Leandro
 */
@RunWith(MockitoJUnitRunner.class)
public class ClienteRepositoryTest {
    
    @Mock
    public ClienteRepositoryProvider provider;

    public ClienteRepositoryTest() {
    }
    
    public void init2() throws Exception {
        ClientePreferencialIDStack stack = mock(ClientePreferencialIDStack.class);
        when(stack.obterID("10")).thenReturn(10);
        when(stack.obterID("1234")).thenReturn(1234);
        when(stack.obterID("789123385")).thenReturn(1);
        when(stack.obterID("5465789")).thenReturn(2);
        when(stack.obterID("5465AAA789")).thenReturn(3);  
        ClienteEventualIDStack stack2 = mock(ClienteEventualIDStack.class);
        when(stack2.obterID("10")).thenReturn(10);
        when(stack2.obterID("1234")).thenReturn(1234);
        when(stack2.obterID("789123385")).thenReturn(1);
        when(stack2.obterID("5465789")).thenReturn(2);
        when(stack2.obterID("5465AAA789")).thenReturn(3);  
        
        
        ClienteRepositoryProvider.OrgPreferencial pref = mock(ClienteRepositoryProvider.OrgPreferencial.class);
        when(pref.getProvider()).thenReturn(provider);
        when(provider.preferencial()).thenReturn(pref);
        MultiMap<String, Void> existPref = new MultiMap<>();
        existPref.put(null, "10", "TESTE1", "1936594564", "19998696369");
        when(provider.preferencial().getContatosExistentes()).thenReturn(existPref);
        
        ClienteRepositoryProvider.OrgEventual evt = mock(ClienteRepositoryProvider.OrgEventual.class);
        when(evt.getProvider()).thenReturn(provider);
        when(provider.eventual()).thenReturn(evt);
        MultiMap<String, Void> existEvt = new MultiMap<>();
        existEvt.put(null, "10", "TESTE1", "1936594564", "19998696369", "teste@teste.com.br");
        when(provider.eventual().getContatosExistentes()).thenReturn(existEvt);
        
        HashMap<Long, Integer> cnpjEventual = new HashMap<>();   
        cnpjEventual.put(10026789000123L, 1);
        when(provider.eventual().getCnpjCadastrados()).thenReturn(cnpjEventual);
        HashMap<Long, Integer> cnpjPreferencial = new HashMap<>();   
        cnpjPreferencial.put(10026789000123L, 1);
        when(provider.preferencial().getCnpjCadastrados()).thenReturn(cnpjPreferencial);
        
        when(provider.getClientePreferencialIDStack(eq(1))).thenReturn(stack);
        when(provider.getClienteEventualIDStack(eq(1))).thenReturn(stack2);
        when(provider.preferencial().getAnteriores()).thenReturn(new MultiMap<String, ClientePreferencialAnteriorVO>()); 
        when(provider.eventual().getAnteriores()).thenReturn(new MultiMap<String, ClienteEventualAnteriorVO>());     
    }

    @Before
    public void init() throws Exception {
        ClientePreferencialIDStack stack = mock(ClientePreferencialIDStack.class);
        when(stack.obterID("10")).thenReturn(10);
        when(stack.obterID("1234")).thenReturn(1234);
        when(stack.obterID("789123385")).thenReturn(1);
        when(stack.obterID("5465789")).thenReturn(2);
        when(stack.obterID("5465AAA789")).thenReturn(3);  
        ClienteEventualIDStack stack2 = mock(ClienteEventualIDStack.class);
        when(stack2.obterID("10")).thenReturn(10);
        when(stack2.obterID("1234")).thenReturn(1234);
        when(stack2.obterID("789123385")).thenReturn(1);
        when(stack2.obterID("5465789")).thenReturn(2);
        when(stack2.obterID("5465AAA789")).thenReturn(3);  
        
        ClienteRepositoryProvider.OrgPreferencial pref = mock(ClienteRepositoryProvider.OrgPreferencial.class);
        when(pref.getProvider()).thenReturn(provider);
        when(provider.preferencial()).thenReturn(pref);
        when(provider.preferencial().getContatosExistentes()).thenReturn(new MultiMap<String, Void>());
        
        ClienteRepositoryProvider.OrgEventual evt = mock(ClienteRepositoryProvider.OrgEventual.class);
        when(evt.getProvider()).thenReturn(provider);
        when(provider.eventual()).thenReturn(evt);
        when(provider.eventual().getContatosExistentes()).thenReturn(new MultiMap<String, Void>());
        
        when(provider.getClientePreferencialIDStack(eq(1))).thenReturn(stack);
        when(provider.getClienteEventualIDStack(eq(1))).thenReturn(stack2);
        when(provider.preferencial().getAnteriores()).thenReturn(new MultiMap<String, ClientePreferencialAnteriorVO>()); 
        when(provider.eventual().getAnteriores()).thenReturn(new MultiMap<String, ClienteEventualAnteriorVO>());     
        
        when(provider.getSistema()).thenReturn("SISTEMA");
        when(provider.getLojaOrigem()).thenReturn("1");
        when(provider.getLojaVR()).thenReturn(1);     
        when(provider.getMunicipioById(3526902)).thenReturn(new MunicipioVO(3526902, "LIMEIRA", new EstadoVO(35, "SP", "SAO PAULO")));
        when(provider.getMunicipioById(3501608)).thenReturn(new MunicipioVO(3501608, "AMERICANA", new EstadoVO(35, "SP", "SAO PAULO")));
        when(provider.getMunicipioById(3538709)).thenReturn(new MunicipioVO(3538709, "PIRACICABA", new EstadoVO(35, "SP", "SAO PAULO")));
        when(provider.getMunicipioByNomeUf("SAO JOSE DO RIO PARDO", "SP")).thenReturn(new MunicipioVO(3549706, "SAO JOSE DO RIO PARDO", new EstadoVO(35, "SP", "SAO PAULO")));
        when(provider.getMunicipioByNomeUf("SAO PAULO", "SP")).thenReturn(new MunicipioVO(3550308, "SAO PAULO", new EstadoVO(35, "SP", "SAO PAULO")));
        when(provider.getMunicipioPadrao()).thenReturn(new MunicipioVO(3526902, "LIMEIRA", new EstadoVO(35, "SP", "SAO PAULO")));
    }
    
    @Test
    public void testOrganizarListagem() throws Exception {
        System.out.print("ClienteRepositoryTest.testOrganizarListagem()...");
        ClienteRepository repository = new ClienteRepository(provider);        
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp5 = ClientePreferencialTestClasses.clienteImp5();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        teste.add(clienteImp5);
        
        List<ClienteIMP> organizado = repository.organizarListagem(teste);
        
        assertEquals(5, organizado.size());
        assertEquals("10", organizado.get(0).getId());
        assertEquals("1234", organizado.get(1).getId());
        assertEquals("789123385", organizado.get(2).getId());
        assertEquals("5465789", organizado.get(3).getId());        
        assertEquals("5465AAA789", organizado.get(4).getId());   
        System.out.println("OK");
    }

    @Test
    public void testConverterClientePreferencial() throws Exception {
        System.out.print("ClienteRepositoryTest.testConverterClientePreferencial()...");
        ClienteRepository repository = new ClienteRepository(provider);  
        
        ClientePreferencialTestClasses.assert1(
                repository.converterClientePreferencial(
                        ClientePreferencialTestClasses.clienteImp1()
                )
        );
        ClientePreferencialTestClasses.assert2(
                repository.converterClientePreferencial(
                        ClientePreferencialTestClasses.clienteImp2()
                )
        );
        ClientePreferencialTestClasses.assert3_2(
                repository.converterClientePreferencial(
                        ClientePreferencialTestClasses.clienteImp3()
                )
        );
        ClientePreferencialTestClasses.assert4(
                repository.converterClientePreferencial(
                        ClientePreferencialTestClasses.clienteImp4()
                )
        );
        ClientePreferencialTestClasses.assert5(
                repository.converterClientePreferencial(
                        ClientePreferencialTestClasses.clienteImp5()
                )
        );
        System.out.println("OK");
    }

    @Test
    public void testConverterClientePreferencialAnterior() throws Exception  {
        System.out.print("ClienteRepositoryTest.testConverterClientePreferencialAnterior()...");
        ClienteRepository repository = new ClienteRepository(provider);  
        
        ClientePreferencialTestClasses.assertAnterior1(
                repository.converterClientePreferencialAnterior(
                        ClientePreferencialTestClasses.clienteImp1()
                ), false
        );
        ClientePreferencialTestClasses.assertAnterior2(
                repository.converterClientePreferencialAnterior(
                        ClientePreferencialTestClasses.clienteImp2()
                ), false
        );
        ClientePreferencialTestClasses.assertAnterior3(
                repository.converterClientePreferencialAnterior(
                        ClientePreferencialTestClasses.clienteImp3()
                ), false
        );
        ClientePreferencialTestClasses.assertAnterior4(
                repository.converterClientePreferencialAnterior(
                        ClientePreferencialTestClasses.clienteImp4()
                ), false
        );
        ClientePreferencialTestClasses.assertAnterior5(
                repository.converterClientePreferencialAnterior(
                        ClientePreferencialTestClasses.clienteImp5()
                ), false
        );
        System.out.println("OK");
    }
    
    @Test
    public void testConverterClienteEventual() throws Exception {
        System.out.print("ClienteRepositoryTest.testConverterClientePreferencial()...");
        ClienteRepository repository = new ClienteRepository(provider);  
        
        ClienteEventualTestClasses.assert1(
                repository.converterClienteEventual(
                        ClientePreferencialTestClasses.clienteImp1()
                )
        );
        ClienteEventualTestClasses.assert2(
                repository.converterClienteEventual(
                        ClientePreferencialTestClasses.clienteImp2()
                )
        );
        ClienteEventualTestClasses.assert3_2(
                repository.converterClienteEventual(
                        ClientePreferencialTestClasses.clienteImp3()
                )
        );
        ClienteEventualTestClasses.assert4(
                repository.converterClienteEventual(
                        ClientePreferencialTestClasses.clienteImp4()
                )
        );
        ClienteEventualTestClasses.assert5(
                repository.converterClienteEventual(
                        ClientePreferencialTestClasses.clienteImp5()
                )
        );
        System.out.println("OK");
    }

    @Test
    public void testConverterClienteEventualAnterior() throws Exception  {
        System.out.print("ClienteRepositoryTest.testConverterClienteEventualAnterior()...");
        ClienteRepository repository = new ClienteRepository(provider);  
        
        ClienteEventualTestClasses.assertAnterior1(
                repository.converterClienteEventualAnterior(
                        ClientePreferencialTestClasses.clienteImp1()
                ), false
        );
        ClienteEventualTestClasses.assertAnterior2(
                repository.converterClienteEventualAnterior(
                        ClientePreferencialTestClasses.clienteImp2()
                ), false
        );
        ClienteEventualTestClasses.assertAnterior3(
                repository.converterClienteEventualAnterior(
                        ClientePreferencialTestClasses.clienteImp3()
                ), false
        );
        ClienteEventualTestClasses.assertAnterior4(
                repository.converterClienteEventualAnterior(
                        ClientePreferencialTestClasses.clienteImp4()
                ), false
        );
        ClienteEventualTestClasses.assertAnterior5(
                repository.converterClienteEventualAnterior(
                        ClientePreferencialTestClasses.clienteImp5()
                ), false
        );
        System.out.println("OK");
    }
    
    @Test
    public void testImportarClientePreferencial() throws Exception {
        System.out.print("ClienteRepositoryTest.testImportarClientePreferencial()...");
        final List<ClientePreferencialVO> pref = new ArrayList<>();
        final List<ClientePreferencialAnteriorVO> ant = new ArrayList<>();
        final List<ClientePreferencialContatoVO> contatos = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            @Override
            public void gravarClientePreferencial(ClientePreferencialVO cliente) {
                pref.add(cliente);
            }
            @Override
            public void gravarClientePreferencialAnterior(ClientePreferencialAnteriorVO anterior) {
                ant.add(anterior);
            }
            
            private int cont = 1;
            @Override
            public void gravarClientePreferencialContato(ClientePreferencialContatoVO contato) throws Exception {
                contato.setId(cont);
                cont++;
                contatos.add(contato);
            }
            
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        
        repository.importarClientePreferencial(teste, EnumSet.noneOf(OpcaoCliente.class));
        
        assertEquals(4, pref.size());
        ClientePreferencialTestClasses.assert1(pref.get(0));
        ClientePreferencialTestClasses.assert3(pref.get(1));
        ClientePreferencialTestClasses.assert2(pref.get(2));
        ClientePreferencialTestClasses.assert4(pref.get(3));
               
        ClientePreferencialTestClasses.assertAnterior1(ant.get(0), true);
        ClientePreferencialTestClasses.assertAnterior3(ant.get(1), true);
        ClientePreferencialTestClasses.assertAnterior2(ant.get(2), true);
        ClientePreferencialTestClasses.assertAnterior4(ant.get(3), true);
        
        assertEquals(4, contatos.size());
        
        ClientePreferencialTestClasses.assertContato1(contatos.get(0));
        ClientePreferencialTestClasses.assertContato2(contatos.get(1));
        ClientePreferencialTestClasses.assertContato3(contatos.get(2));
        ClientePreferencialTestClasses.assertContato4(contatos.get(3));
        System.out.println("OK");
    }
    
    @Test
    public void testImportarClienteEventual() throws Exception {
        System.out.print("ClienteRepositoryTest.testImportarClienteEventual()...");
        final List<ClienteEventualVO> pref = new ArrayList<>();
        final List<ClienteEventualAnteriorVO> ant = new ArrayList<>();
        final List<ClienteEventualContatoVO> contatos = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            @Override
            public void gravarClienteEventual(ClienteEventualVO cliente) {
                pref.add(cliente);
            }
            @Override
            public void gravarClienteEventualAnterior(ClienteEventualAnteriorVO anterior) {
                ant.add(anterior);
            }
            
            private int cont = 1;
            @Override
            public void gravarClienteEventualContato(ClienteEventualContatoVO contato) throws Exception {
                contato.setId(cont);
                cont++;
                contatos.add(contato);
            }
            
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        
        repository.importarClienteEventual(teste, EnumSet.noneOf(OpcaoCliente.class));
        
        assertEquals(4, pref.size());
        ClienteEventualTestClasses.assert1(pref.get(0));
        ClienteEventualTestClasses.assert3(pref.get(1));
        ClienteEventualTestClasses.assert2(pref.get(2));
        ClienteEventualTestClasses.assert4(pref.get(3));
               
        ClienteEventualTestClasses.assertAnterior1(ant.get(0), true);
        ClienteEventualTestClasses.assertAnterior3(ant.get(1), true);
        ClienteEventualTestClasses.assertAnterior2(ant.get(2), true);
        ClienteEventualTestClasses.assertAnterior4(ant.get(3), true);
        
        assertEquals(4, contatos.size());
        
        ClienteEventualTestClasses.assertContato1(contatos.get(0));
        ClienteEventualTestClasses.assertContato2(contatos.get(1));
        ClienteEventualTestClasses.assertContato3(contatos.get(2));
        ClienteEventualTestClasses.assertContato4(contatos.get(3));
        System.out.println("OK");
    }
    
    @Test
    public void testImportarClienteEventual2() throws Exception {
        System.out.print("ClienteRepositoryTest.testImportarClienteEventual2()...");
        final List<ClienteEventualVO> pref = new ArrayList<>();
        final List<ClienteEventualAnteriorVO> ant = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            @Override
            public void gravarClienteEventual(ClienteEventualVO cliente) {
                pref.add(cliente);
            }
            @Override
            public void gravarClienteEventualAnterior(ClienteEventualAnteriorVO anterior) {
                ant.add(anterior);
            }
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        MultiMap<String, ClienteEventualAnteriorVO> multiMap = new MultiMap<>();
        
        ClienteEventualAnteriorVO anterior = repository.converterClienteEventualAnterior(ClientePreferencialTestClasses.clienteImp3());
        ClienteEventualVO cl = new ClienteEventualVO();
        cl.setId(1);
        anterior.setCodigoAtual(cl);
        multiMap.put(anterior, anterior.getSistema(), anterior.getLoja(), anterior.getId());
        
        when(provider.eventual().getAnteriores()).thenReturn(multiMap);
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        
        repository.importarClienteEventual(teste, EnumSet.noneOf(OpcaoCliente.class));
        
        assertEquals(3, pref.size());
        ClienteEventualTestClasses.assert1(pref.get(0));
        ClienteEventualTestClasses.assert2(pref.get(1));
        ClienteEventualTestClasses.assert4(pref.get(2));
               
        ClienteEventualTestClasses.assertAnterior1(ant.get(0), true);
        ClienteEventualTestClasses.assertAnterior2(ant.get(1), true);
        ClienteEventualTestClasses.assertAnterior4(ant.get(2), true);        
        System.out.println("OK");
    }
    
    @Test
    public void testImportarContatosPreferencial() throws Exception {
        System.out.print("ClienteRepositoryTest.testImportarContatosPreferencial()...");
        final List<ClientePreferencialContatoVO> contatos = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            private int cont = 1;
            @Override
            public void gravarClientePreferencialContato(ClientePreferencialContatoVO contato) throws Exception {
                contato.setId(cont);
                cont++;
                contatos.add(contato);
            }
            
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        
        repository.importarClientePreferencial(teste, EnumSet.noneOf(OpcaoCliente.class));
        
        assertEquals(4, contatos.size());
        
        ClientePreferencialTestClasses.assertContato1(contatos.get(0));
        ClientePreferencialTestClasses.assertContato2(contatos.get(1));
        ClientePreferencialTestClasses.assertContato3(contatos.get(2));
        ClientePreferencialTestClasses.assertContato4(contatos.get(3));
        System.out.println("OK");
    }
    
    @Test
    public void testImportarContatosEventual() throws Exception {
        System.out.print("ClienteRepositoryTest.testImportarContatosEventual()...");
        final List<ClienteEventualContatoVO> contatos = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            private int cont = 1;
            @Override
            public void gravarClienteEventualContato(ClienteEventualContatoVO contato) throws Exception {
                contato.setId(cont);
                cont++;
                contatos.add(contato);
            }
            
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        
        repository.importarClienteEventual(teste, EnumSet.noneOf(OpcaoCliente.class));
        
        assertEquals(4, contatos.size());
        
        ClienteEventualTestClasses.assertContato1(contatos.get(0));
        ClienteEventualTestClasses.assertContato2(contatos.get(1));
        ClienteEventualTestClasses.assertContato3(contatos.get(2));
        ClienteEventualTestClasses.assertContato4(contatos.get(3));
        System.out.println("OK");
    }
    
    @Test
    public void testImportarClientePreferencial2() throws Exception {
        System.out.print("ClienteRepositoryTest.testImportarClientePreferencial2()...");
        final List<ClientePreferencialVO> pref = new ArrayList<>();
        final List<ClientePreferencialAnteriorVO> ant = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            @Override
            public void gravarClientePreferencial(ClientePreferencialVO cliente) {
                pref.add(cliente);
            }
            @Override
            public void gravarClientePreferencialAnterior(ClientePreferencialAnteriorVO anterior) {
                ant.add(anterior);
            }
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        MultiMap<String, ClientePreferencialAnteriorVO> multiMap = new MultiMap<>();
        
        ClientePreferencialAnteriorVO anterior = repository.converterClientePreferencialAnterior(ClientePreferencialTestClasses.clienteImp3());
        ClientePreferencialVO cl = new ClientePreferencialVO();
        cl.setId(1);
        anterior.setCodigoAtual(cl);
        multiMap.put(anterior, anterior.getSistema(), anterior.getLoja(), anterior.getId());
        
        when(provider.preferencial().getAnteriores()).thenReturn(multiMap);
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        
        repository.importarClientePreferencial(teste, EnumSet.noneOf(OpcaoCliente.class));
        
        assertEquals(3, pref.size());
        ClientePreferencialTestClasses.assert1(pref.get(0));
        ClientePreferencialTestClasses.assert2(pref.get(1));
        ClientePreferencialTestClasses.assert4(pref.get(2));
               
        ClientePreferencialTestClasses.assertAnterior1(ant.get(0), true);
        ClientePreferencialTestClasses.assertAnterior2(ant.get(1), true);
        ClientePreferencialTestClasses.assertAnterior4(ant.get(2), true);        
        System.out.println("OK");
    }
    
    @Test
    public void testUnificarPreferencial() throws Exception {
        System.out.print("ClienteRepositoryTest.testUnificarPreferencial()...");
        init2();
        final List<ClientePreferencialVO> pref = new ArrayList<>();
        final List<ClientePreferencialAnteriorVO> ant = new ArrayList<>();
        final List<ClientePreferencialContatoVO> contatos = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            @Override
            public void gravarClientePreferencial(ClientePreferencialVO cliente) {
                pref.add(cliente);
            }
            @Override
            public void gravarClientePreferencialAnterior(ClientePreferencialAnteriorVO anterior) {
                ant.add(anterior);
            }

            private int cont = 2;
            @Override
            public void gravarClientePreferencialContato(ClientePreferencialContatoVO contato) throws Exception {
                contato.setId(cont);
                cont++;
                contatos.add(contato);
            }
            
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        
        repository.unificarClientePreferencial(teste);
        
        assertEquals(3, pref.size());
        ClientePreferencialTestClasses.assert1(pref.get(0));
        ClientePreferencialTestClasses.assert3(pref.get(1));
        ClientePreferencialTestClasses.assert4(pref.get(2));
               
        ClientePreferencialTestClasses.assertAnterior1(ant.get(0), true);
        ClientePreferencialTestClasses.assertAnterior3(ant.get(1), true);
        ClientePreferencialTestClasses.assertAnterior2(ant.get(2), true);
        ClientePreferencialTestClasses.assertAnterior4(ant.get(3), true);
        
        assertEquals(3, contatos.size());
        
        ClientePreferencialTestClasses.assertContato2(contatos.get(0));
        ClientePreferencialTestClasses.assertContato3(contatos.get(1));
        ClientePreferencialTestClasses.assertContato4(contatos.get(2));
        System.out.println("OK");
    }
    
    @Test
    public void testUnificarEventual() throws Exception {
        System.out.print("ClienteRepositoryTest.testUnificarEventual()...");
        init2();
        final List<ClienteEventualVO> pref = new ArrayList<>();
        final List<ClienteEventualAnteriorVO> ant = new ArrayList<>();
        final List<ClienteEventualContatoVO> contatos = new ArrayList<>();
        ClienteRepository repository = new ClienteRepository(provider) {
            @Override
            public void gravarClienteEventual(ClienteEventualVO cliente) {
                pref.add(cliente);
            }
            @Override
            public void gravarClienteEventualAnterior(ClienteEventualAnteriorVO anterior) {
                ant.add(anterior);
            }
            
            private int cont = 2;
            @Override
            public void gravarClienteEventualContato(ClienteEventualContatoVO contato) throws Exception {
                contato.setId(cont);
                cont++;
                contatos.add(contato);
            }
            
            @Override
            public void setNotificacao(String mensagem, int qtd) throws Exception {}
            @Override
            public void notificar() throws Exception {}
        };
        
        ClienteIMP clienteImp1 = ClientePreferencialTestClasses.clienteImp1();
        ClienteIMP clienteImp2 = ClientePreferencialTestClasses.clienteImp2();
        ClienteIMP clienteImp3 = ClientePreferencialTestClasses.clienteImp3();
        ClienteIMP clienteImp4 = ClientePreferencialTestClasses.clienteImp4();
        ClienteIMP clienteImp11 = ClientePreferencialTestClasses.clienteImp1();
        
        List<ClienteIMP> teste = new ArrayList<>();
        
        teste.add(clienteImp1);
        teste.add(clienteImp2);
        teste.add(clienteImp3);
        teste.add(clienteImp4);
        teste.add(clienteImp11);
        
        repository.unificarClienteEventual(teste);
        
        assertEquals(3, pref.size());
        ClienteEventualTestClasses.assert1(pref.get(0));
        ClienteEventualTestClasses.assert3(pref.get(1));
        ClienteEventualTestClasses.assert4(pref.get(2));
               
        ClienteEventualTestClasses.assertAnterior1(ant.get(0), true);
        ClienteEventualTestClasses.assertAnterior3(ant.get(1), true);
        ClienteEventualTestClasses.assertAnterior2(ant.get(2), true);
        ClienteEventualTestClasses.assertAnterior4(ant.get(3), true);
        
        assertEquals(3, contatos.size());
        
        ClienteEventualTestClasses.assertContato2(contatos.get(0));
        ClienteEventualTestClasses.assertContato3(contatos.get(1));
        ClienteEventualTestClasses.assertContato4(contatos.get(2));
        
        System.out.println("OK");
    }
    
    
}
