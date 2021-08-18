package vrimplantacao2_5.service.cadastro.sistemabancodados;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.cadastro.sistemabancodados.SistemaBancoDadosDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.SistemaBancoDadosVO;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaBancoDadosServiceTest {

    @Test
    public void testInserir() throws Exception {
        SistemaBancoDadosDAO sistemBancoDadosDAO = mock(SistemaBancoDadosDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaBancoDadosService sistemaBancoDadosService = new SistemaBancoDadosService(sistemBancoDadosDAO, provider);
        
        SistemaBancoDadosVO vo = new SistemaBancoDadosVO();
        vo.setIdSistema(1);
        vo.setIdBancoDados(1);
        vo.setNomeSchema("banco");
        vo.setPorta(1400);
        vo.setUsuario("user");
        vo.setSenha("pass");
        
        sistemaBancoDadosService.inserir(vo);
        
        Mockito.verify(sistemBancoDadosDAO, Mockito.times(1)).inserir(vo);
    }
    
    @Test
    public void testAlterar() throws Exception {
        SistemaBancoDadosDAO sistemBancoDadosDAO = mock(SistemaBancoDadosDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaBancoDadosService sistemaBancoDadosService = new SistemaBancoDadosService(sistemBancoDadosDAO, provider);
        
        SistemaBancoDadosVO vo = new SistemaBancoDadosVO();
        vo.setIdSistema(1);
        vo.setIdBancoDados(1);
        vo.setNomeSchema("banco");
        vo.setPorta(1400);
        vo.setUsuario("user");
        vo.setSenha("pass");
        
        sistemaBancoDadosService.alterar(vo);
        
        Mockito.verify(sistemBancoDadosDAO, Mockito.times(1)).alterar(vo);
    }
    
    @Test
    public void testConsultar() throws Exception {
        SistemaBancoDadosDAO sistemBancoDadosDAO = mock(SistemaBancoDadosDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        SistemaBancoDadosService sistemaBancoDadosService = new SistemaBancoDadosService(sistemBancoDadosDAO, provider);

        List<SistemaBancoDadosVO> result = new ArrayList<>();
        SistemaBancoDadosVO vo = new SistemaBancoDadosVO();
        vo.setIdBancoDados(2);
        
        result.add(vo);
        
        when(sistemBancoDadosDAO.consultar(vo)).thenReturn(result);
        
        assertEquals(1, sistemaBancoDadosService.consultar(vo).size());
    }
}
