package vrimplantacao2.dao.cadastro.cliente;

import static junit.framework.Assert.*;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.importacao.ClienteIMP;

public class ClientePreferencialTestClasses {
    
    //<editor-fold defaultstate="collapsed" desc="Cliente 1">
    
    public static ClienteIMP clienteImp1() {
        ClienteIMP imp = new ClienteIMP("10", "192.168.789-69", "12.345.670-5", "RAZAO TESTE 1", "FANTASIA TESTE 1");
        imp.setEndereco("endereço teste");
        imp.setNumero("456");
        imp.setBairro("LAGO");
        imp.setMunicipioIBGE(3526902);
        imp.setCep("13456789");
        imp.setEmpresaEndereco("endereco empresa teste");
        imp.setEmpresaNumero("123");
        imp.setEmpresaBairro("LAGO2");
        imp.setEmpresaMunicipio("SÃO PAULO");
        imp.setEmpresaUf("SP");
        imp.setEmpresaCep("13456789");
        imp.setCobrancaEndereco("endereco empresa teste");
        imp.setCobrancaNumero("123");
        imp.setCobrancaBairro("LAGO2");
        imp.setCobrancaMunicipio("SÃO PAULO");
        imp.setCobrancaUf("SP");
        imp.setCobrancaCep("13456789");
        imp.addContato("1", "TESTE1", "(19)3659-4564", "(19)9-9869-6369", "teste@teste.com.br");
        imp.addContato("2", "TESTE2", "(19)3659-4564", null, null);
        imp.addContato("3", "TESTE3", null, null, "teste@teste.com.br");
        imp.addContato("4", "TESTE4", null, "(19)9-9869-6369", null);
        imp.addContato("4", "TESTE4", null, "19998696369", null);
        return imp;
    }
    public static void assert1(ClientePreferencialVO get) {        
        assertEquals(19216878969L, get.getCnpj());
        assertEquals("12.345.670-5", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 1", get.getNome());
        assertEquals("ENDERECO TESTE", get.getEndereco());
        assertEquals("456", get.getNumero());
        assertEquals("LAGO", get.getBairro());
        assertEquals(3526902, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(13456789, get.getCep());
        assertEquals("ENDERECO EMPRESA TESTE", get.getEnderecoEmpresa());
        assertEquals("123", get.getNumeroEmpresa());
        assertEquals("LAGO2", get.getBairroEmpresa());
        assertEquals(3550308, get.getId_municipioEmpresa());
        assertEquals(35, get.getId_estadoEmpresa()); 
        assertEquals(13456789, get.getCepEmpresa());       
    }
    public static void assertAnterior1(ClientePreferencialAnteriorVO get, boolean checarAtual) {
        assertEquals("SISTEMA", get.getSistema());
        assertEquals("1", get.getLoja());
        assertEquals("10", get.getId());
        if (checarAtual) {
            assertNotNull(get.getCodigoAtual());
            assertEquals(10, get.getCodigoAtual().getId());
        }
        assertEquals("192.168.789-69", get.getCnpj());
        assertEquals("12.345.670-5", get.getIe());
        assertEquals("RAZAO TESTE 1", get.getNome());
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Cliente 2">
    
    public static ClienteIMP clienteImp2() {
        ClienteIMP imp = new ClienteIMP("789123385", "10.026.789/0001-23", "789456123546", "RAZAO TESTE 2", "FANTASIA TESTE 2");
        imp.setEndereco("endereco 2");
        imp.setNumero("456");
        imp.setBairro("BAIRRO 2 ");
        imp.setMunicipio("SÃO JOSÉ DO RIO PARDO");
        imp.setUf("sp");
        imp.setCep("78945123");
        imp.setCobrancaEndereco("endereco 23");
        imp.setCobrancaNumero("4564");
        imp.setCobrancaBairro("BAIRRO 23 ");
        imp.setCobrancaMunicipio("SÃO JOSÉ DO RIO PARDO");
        imp.setCobrancaUf("sp");
        imp.setCobrancaCep("78945123");
        return imp;
    }
    public static void assert2(ClientePreferencialVO get) {
        assertEquals(10026789000123L, get.getCnpj());
        assertEquals("789456123546", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 2", get.getNome());
        assertEquals("ENDERECO 2", get.getEndereco());
        assertEquals("456", get.getNumero());
        assertEquals("BAIRRO 2", get.getBairro());
        assertEquals(3549706, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(78945123, get.getCep());
        assertEquals("SEM ENDERECO", get.getEnderecoEmpresa());
        assertEquals("0", get.getNumeroEmpresa());
        assertEquals("SEM BAIRRO", get.getBairroEmpresa());
        assertEquals(3526902, get.getId_municipioEmpresa());
        assertEquals(35, get.getId_estadoEmpresa());  
        assertEquals(0, get.getCepEmpresa()); 
    }
    public static void assertAnterior2(ClientePreferencialAnteriorVO get, boolean checarAtual) {        
        assertEquals("SISTEMA", get.getSistema());
        assertEquals("1", get.getLoja());
        assertEquals("789123385", get.getId());
        if (checarAtual) {
            assertNotNull(get.getCodigoAtual());
            assertEquals(1, get.getCodigoAtual().getId());
        }
        assertEquals("10.026.789/0001-23", get.getCnpj());
        assertEquals("789456123546", get.getIe());
        assertEquals("RAZAO TESTE 2", get.getNome());
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Cliente 3">
    
    public static ClienteIMP clienteImp3() {
        ClienteIMP imp = new ClienteIMP("1234", "582638950023583", "ISENTO", "RAZAO TESTE 3", "FANTASIA TESTE 3");
        return imp;
    }
    public static void assert3(ClientePreferencialVO get) {
        assertEquals(1234L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 3", get.getNome());
        assertEquals("SEM ENDERECO", get.getEndereco());
        assertEquals("0", get.getNumero());
        assertEquals("SEM BAIRRO", get.getBairro());
        assertEquals(3526902, get.getId_municipio());
        assertEquals(35, get.getId_estado()); 
        assertEquals("SEM ENDERECO", get.getEnderecoEmpresa());
        assertEquals("0", get.getNumeroEmpresa());
        assertEquals("SEM BAIRRO", get.getBairroEmpresa());
        assertEquals(3526902, get.getId_municipioEmpresa());
        assertEquals(35, get.getId_estadoEmpresa()); 
    }
    public static void assert3_2(ClientePreferencialVO get) {
        assertEquals(0L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 3", get.getNome());
        assertEquals("SEM ENDERECO", get.getEndereco());
        assertEquals("0", get.getNumero());
        assertEquals("SEM BAIRRO", get.getBairro());
        assertEquals(3526902, get.getId_municipio());
        assertEquals(35, get.getId_estado()); 
        assertEquals("SEM ENDERECO", get.getEnderecoEmpresa());
        assertEquals("0", get.getNumeroEmpresa());
        assertEquals("SEM BAIRRO", get.getBairroEmpresa());
        assertEquals(3526902, get.getId_municipioEmpresa());
        assertEquals(35, get.getId_estadoEmpresa()); 
    }
    public static void assertAnterior3(ClientePreferencialAnteriorVO get, boolean checarAtual) {        
        assertEquals("SISTEMA", get.getSistema());
        assertEquals("1", get.getLoja());
        assertEquals("1234", get.getId());
        if (checarAtual) {
            assertNotNull(get.getCodigoAtual());
            assertEquals(1234, get.getCodigoAtual().getId());
        }
        assertEquals("582638950023583", get.getCnpj());
        assertEquals("ISENTO", get.getIe());
        assertEquals("RAZAO TESTE 3", get.getNome());
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Cliente 4">
    
    public static ClienteIMP clienteImp4() {
        ClienteIMP imp = new ClienteIMP("5465789", "269.895.487-26", null, "RAZAO TESTE 4", "FANTASIA TESTE 4");
        imp.setEndereco("ENDERECO 4");
        imp.setNumero("789");
        imp.setBairro("BAIRRO 4");
        imp.setMunicipioIBGE(3501608);
        imp.setCep("13456789");
        imp.copiarEnderecoParaEmpresa();
        imp.copiarEnderecoParaCobranca();
        return imp;
    }
    public static void assert4(ClientePreferencialVO get) {
        assertEquals(26989548726L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 4", get.getNome());
        assertEquals("ENDERECO 4", get.getEndereco());
        assertEquals("789", get.getNumero());
        assertEquals("BAIRRO 4", get.getBairro());
        assertEquals(3501608, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(13456789, get.getCep());
        assertEquals("ENDERECO 4", get.getEnderecoEmpresa());
        assertEquals("789", get.getNumeroEmpresa());
        assertEquals("BAIRRO 4", get.getBairroEmpresa());
        assertEquals(3501608, get.getId_municipioEmpresa());
        assertEquals(35, get.getId_estadoEmpresa());  
        assertEquals(13456789, get.getCepEmpresa()); 
    }
    public static void assertAnterior4(ClientePreferencialAnteriorVO get, boolean checarAtual) {
        assertEquals("SISTEMA", get.getSistema());
        assertEquals("1", get.getLoja());
        assertEquals("5465789", get.getId());
        if (checarAtual) {
            assertNotNull(get.getCodigoAtual());
            assertEquals(2, get.getCodigoAtual().getId());
        }
        assertEquals("269.895.487-26", get.getCnpj());
        assertNull(get.getIe());
        assertEquals("RAZAO TESTE 4", get.getNome());
    }
    
    //</editor-fold>   
    
    //<editor-fold defaultstate="collapsed" desc="Cliente 5">
    
    public static ClienteIMP clienteImp5() {
        ClienteIMP imp = new ClienteIMP("5465AAA789", "269.895.487-26", null, "RAZAO TESTE 5", "FANTASIA TESTE 5");
        imp.setEndereco("endereco 5");
        imp.setNumero("789456456");
        imp.setBairro("São Cristovão ");
        imp.setMunicipio("São Paulo");
        imp.setUf("sp");
        imp.setCep("45612378");
        imp.setEmpresaEndereco("endereco empresa 5");
        imp.setEmpresaNumero("147");
        imp.setEmpresaBairro("ADÉLIA");
        imp.setEmpresaMunicipio("SÃO JOSÉ DO RIO PARDO");
        imp.setEmpresaUf("sp");
        imp.setEmpresaCep("78945123");
        imp.setCobrancaEndereco("endereco empresa 5");
        imp.setCobrancaNumero("147");
        imp.setCobrancaBairro("ADÉLIA");
        imp.setCobrancaMunicipio("SÃO JOSÉ DO RIO PARDO");
        imp.setCobrancaUf("sp");
        imp.setCobrancaCep("78945123");
        return imp;
    }
    public static void assert5(ClientePreferencialVO get) {
        assertEquals(26989548726L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 5", get.getNome());
        assertEquals("ENDERECO 5", get.getEndereco());
        assertEquals("789456", get.getNumero());
        assertEquals("SAO CRISTOVAO", get.getBairro());
        assertEquals(3550308, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(45612378, get.getCep());
        assertEquals("ENDERECO EMPRESA 5", get.getEnderecoEmpresa());
        assertEquals("147", get.getNumeroEmpresa());
        assertEquals("ADELIA", get.getBairroEmpresa());
        assertEquals(3549706, get.getId_municipioEmpresa());
        assertEquals(35, get.getId_estadoEmpresa());  
        assertEquals(78945123, get.getCepEmpresa()); 
    }
    public static void assertAnterior5(ClientePreferencialAnteriorVO get, boolean checarAtual) {
        assertEquals("SISTEMA", get.getSistema());
        assertEquals("1", get.getLoja());
        assertEquals("5465AAA789", get.getId());
        if (checarAtual) {
            assertNotNull(get.getCodigoAtual());
            assertEquals(3, get.getCodigoAtual().getId());
        }
        assertEquals("269.895.487-26", get.getCnpj());
        assertNull(get.getIe());
        assertEquals("RAZAO TESTE 5", get.getNome());
    }
    
    //</editor-fold>

    public static void assertContato1(ClientePreferencialContatoVO contato) {
        assertEquals(1, contato.getId());
        assertEquals(10, contato.getIdClientePreferencial());
        assertEquals("TESTE1", contato.getNome());
        assertEquals("1936594564", contato.getTelefone());
        assertEquals("19998696369", contato.getCelular());
    }
    
    public static void assertContato2(ClientePreferencialContatoVO contato) {
        assertEquals(2, contato.getId());
        assertEquals(10, contato.getIdClientePreferencial());
        assertEquals("TESTE2", contato.getNome());
        assertEquals("1936594564", contato.getTelefone());
        assertEquals("", contato.getCelular());
    }
    
    public static void assertContato3(ClientePreferencialContatoVO contato) {
        assertEquals(3, contato.getId());
        assertEquals(10, contato.getIdClientePreferencial());
        assertEquals("TESTE3", contato.getNome());
        assertEquals("", contato.getTelefone());
        assertEquals("", contato.getCelular());
    }
    
    public static void assertContato4(ClientePreferencialContatoVO contato) {
        assertEquals(4, contato.getId());
        assertEquals(10, contato.getIdClientePreferencial());
        assertEquals("TESTE4", contato.getNome());
        assertEquals("", contato.getTelefone());
        assertEquals("19998696369", contato.getCelular());
    }

}
