package vrimplantacao2.dao.cadastro.cliente;

import static junit.framework.Assert.*;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;

class ClienteEventualTestClasses {
    
    //<editor-fold defaultstate="collapsed" desc="Cliente 1">
    
    static void assert1(ClienteEventualVO get) {        
        assertEquals(19216878969L, get.getCnpj());
        assertEquals("12.345.670-5", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 1", get.getNome());
        assertEquals("ENDERECO TESTE", get.getEndereco());
        assertEquals("456", get.getNumero());
        assertEquals("LAGO", get.getBairro());
        assertEquals(3526902, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(13456789, get.getCep());
        assertEquals("ENDERECO EMPRESA TESTE", get.getEnderecoCobranca());
        assertEquals("123", get.getNumeroCobranca());
        assertEquals("LAGO2", get.getBairroCobranca());
        assertEquals(3550308, get.getId_municipioCobranca());
        assertEquals(35, get.getId_estadoCobranca()); 
        assertEquals(13456789, get.getCepCobranca());       
    }
    
    static void assertAnterior1(ClienteEventualAnteriorVO get, boolean checarAtual) {
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

    static void assert2(ClienteEventualVO get) {
        assertEquals(10026789000123L, get.getCnpj());
        assertEquals("789456123546", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 2", get.getNome());
        assertEquals("ENDERECO 2", get.getEndereco());
        assertEquals("456", get.getNumero());
        assertEquals("BAIRRO 2", get.getBairro());
        assertEquals(3549706, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(78945123, get.getCep());
        assertEquals("ENDERECO 23", get.getEnderecoCobranca());
        assertEquals("4564", get.getNumeroCobranca());
        assertEquals("BAIRRO 23", get.getBairroCobranca());
        assertEquals(3549706, get.getId_municipioCobranca());
        assertEquals(35, get.getId_estadoCobranca());  
        assertEquals(78945123, get.getCepCobranca()); 
    }
    static void assertAnterior2(ClienteEventualAnteriorVO get, boolean checarAtual) {        
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
    
    static void assert3(ClienteEventualVO get) {
        assertEquals(1234L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 3", get.getNome());
        assertEquals("SEM ENDERECO", get.getEndereco());
        assertEquals("0", get.getNumero());
        assertEquals("SEM BAIRRO", get.getBairro());
        assertEquals(3526902, get.getId_municipio());
        assertEquals(35, get.getId_estado()); 
        assertEquals("SEM ENDERECO", get.getEnderecoCobranca());
        assertEquals("0", get.getNumeroCobranca());
        assertEquals("SEM BAIRRO", get.getBairroCobranca());
        assertEquals(3526902, get.getId_municipioCobranca());
        assertEquals(35, get.getId_estadoCobranca()); 
    }
    static void assert3_2(ClienteEventualVO get) {
        assertEquals(0L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 3", get.getNome());
        assertEquals("SEM ENDERECO", get.getEndereco());
        assertEquals("0", get.getNumero());
        assertEquals("SEM BAIRRO", get.getBairro());
        assertEquals(3526902, get.getId_municipio());
        assertEquals(35, get.getId_estado()); 
        assertEquals("SEM ENDERECO", get.getEnderecoCobranca());
        assertEquals("0", get.getNumeroCobranca());
        assertEquals("SEM BAIRRO", get.getBairroCobranca());
        assertEquals(3526902, get.getId_municipioCobranca());
        assertEquals(35, get.getId_estadoCobranca()); 
    }
    static void assertAnterior3(ClienteEventualAnteriorVO get, boolean checarAtual) {        
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
    
    static void assert4(ClienteEventualVO get) {
        assertEquals(26989548726L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 4", get.getNome());
        assertEquals("ENDERECO 4", get.getEndereco());
        assertEquals("789", get.getNumero());
        assertEquals("BAIRRO 4", get.getBairro());
        assertEquals(3501608, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(13456789, get.getCep());
        assertEquals("ENDERECO 4", get.getEnderecoCobranca());
        assertEquals("789", get.getNumeroCobranca());
        assertEquals("BAIRRO 4", get.getBairroCobranca());
        assertEquals(3501608, get.getId_municipioCobranca());
        assertEquals(35, get.getId_estadoCobranca());  
        assertEquals(13456789, get.getCepCobranca()); 
    }
    
    static void assertAnterior4(ClienteEventualAnteriorVO get, boolean checarAtual) {
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

    static void assert5(ClienteEventualVO get) {
        assertEquals(26989548726L, get.getCnpj());
        assertEquals("ISENTO", get.getInscricaoEstadual());
        assertEquals("RAZAO TESTE 5", get.getNome());
        assertEquals("ENDERECO 5", get.getEndereco());
        assertEquals("789456", get.getNumero());
        assertEquals("SAO CRISTOVAO", get.getBairro());
        assertEquals(3550308, get.getId_municipio());
        assertEquals(35, get.getId_estado());
        assertEquals(45612378, get.getCep());
        assertEquals("ENDERECO EMPRESA 5", get.getEnderecoCobranca());
        assertEquals("147", get.getNumeroCobranca());
        assertEquals("ADELIA", get.getBairroCobranca());
        assertEquals(3549706, get.getId_municipioCobranca());
        assertEquals(35, get.getId_estadoCobranca());  
        assertEquals(78945123, get.getCepCobranca()); 
    }
    static void assertAnterior5(ClienteEventualAnteriorVO get, boolean checarAtual) {
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

    public static void assertContato1(ClienteEventualContatoVO contato) {
        assertEquals(1, contato.getId());
        assertEquals(10, contato.getIdClienteEventual());
        assertEquals("TESTE1", contato.getNome());
        assertEquals("1936594564", contato.getTelefone());
        assertEquals("19998696369", contato.getCelular());
    }
    
    public static void assertContato2(ClienteEventualContatoVO contato) {
        assertEquals(2, contato.getId());
        assertEquals(10, contato.getIdClienteEventual());
        assertEquals("TESTE2", contato.getNome());
        assertEquals("1936594564", contato.getTelefone());
        assertEquals("0", contato.getCelular());
    }
    
    public static void assertContato3(ClienteEventualContatoVO contato) {
        assertEquals(3, contato.getId());
        assertEquals(10, contato.getIdClienteEventual());
        assertEquals("TESTE3", contato.getNome());
        assertEquals("0", contato.getTelefone());
        assertEquals("0", contato.getCelular());
    }
    
    public static void assertContato4(ClienteEventualContatoVO contato) {
        assertEquals(4, contato.getId());
        assertEquals(10, contato.getIdClienteEventual());
        assertEquals("TESTE4", contato.getNome());
        assertEquals("0", contato.getTelefone());
        assertEquals("19998696369", contato.getCelular());
    }

}
