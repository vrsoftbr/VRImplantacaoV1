package vrimplantacao2_5.service.cadastro.usuario;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import vrimplantacao2_5.dao.cadastro.unidade.UnidadeDAO;
import vrimplantacao2_5.dao.cadastro.usuario.UsuarioDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.service.cadastro.unidade.UnidadeService;
import vrimplantacao2_5.vo.cadastro.UnidadeVO;
import vrimplantacao2_5.vo.cadastro.UsuarioVO;

/**
 *
 * @author Desenvolvimento
 */
public class UsuarioServiceTest {

    @Test
    public void testExisteUsuario() throws Exception {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);
        
        UsuarioVO vo = new UsuarioVO();
        vo.setLogin("LUCAS");
        vo.setIdUnidade(1);
        
        when(usuarioDAO.existeUsuario(vo)).thenReturn(true);
        
        assertEquals(true, usuarioDAO.existeUsuario(vo));        
    }
    
    @Test
    public void testInserir() throws Exception {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UsuarioService usuarioService = new UsuarioService(usuarioDAO, provider);
        
        UsuarioVO usuarioVO = new UsuarioVO();
        usuarioVO.setNome("MIGRACAO");
        usuarioVO.setLogin("VRMIGRACAO");
        usuarioVO.setSenha("123");
        usuarioVO.setIdUnidade(1);
        
        usuarioService.inserir(usuarioVO);
        
        when(usuarioDAO.existeUsuario(usuarioVO)).thenReturn(true);
        
        Mockito.verify(usuarioDAO, Mockito.times(1)).inserir(usuarioVO);        
    }
    
    @Test
    public void testAlterar() throws Exception {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UsuarioService usuarioService = new UsuarioService(usuarioDAO, provider);
        
        UsuarioVO usuarioVO = new UsuarioVO();
        usuarioVO.setNome("MIGRACAO");
        usuarioVO.setLogin("VRMIGRACAO");
        usuarioVO.setSenha("123");
        usuarioVO.setIdUnidade(1);
        
        usuarioService.alterar(usuarioVO);       
        
        Mockito.verify(usuarioDAO, Mockito.times(1)).alterar(usuarioVO);
    }
    
    @Test
    public void testConsultar() throws Exception {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UsuarioService usuarioService = new UsuarioService(usuarioDAO, provider);
        
        List<UsuarioVO> result = new ArrayList<>();
        UsuarioVO usuarioVO = new UsuarioVO();
        usuarioVO.setNome("LUCAS");
        usuarioVO.setIdUnidade(1);
        result.add(usuarioVO);
        
        when(usuarioDAO.consultar(usuarioVO)).thenReturn(result);
        
        assertEquals(1, usuarioService.consultar(usuarioVO).size());
    }
    
    @Test
    public void testGetUsuarios() throws Exception {
        UsuarioDAO usuarioDAO = mock(UsuarioDAO.class);
        ConexaoProvider provider = mock(ConexaoProvider.class);
        UsuarioService usuarioService = new UsuarioService(usuarioDAO, provider);
        
        List<UsuarioVO> result = new ArrayList<>();
        UsuarioVO usuarioVO = new UsuarioVO();
        result.add(usuarioVO);
        
        when(usuarioDAO.getUsuario()).thenReturn(result);
        
        assertEquals(1, usuarioService.getUsuario().size());
    }
}
