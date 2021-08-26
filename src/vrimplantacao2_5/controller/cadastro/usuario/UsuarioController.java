package vrimplantacao2_5.controller.cadastro.usuario;

import java.util.List;
import vrimplantacao2_5.gui.cadastro.usuario.ConsultaUsuarioGUI;
import vrimplantacao2_5.service.cadastro.usuario.UsuarioService;
import vrimplantacao2_5.vo.cadastro.UsuarioVO;

/**
 *
 * @author Desenvolvimento
 */
public class UsuarioController {

    private final UsuarioService usuarioService = new UsuarioService();
    private ConsultaUsuarioGUI consultaUsuarioGUI;
    private List<UsuarioVO> usuarioVO = null;
    
    public UsuarioController() {
        
    }
    
    public UsuarioController(ConsultaUsuarioGUI consultaUsuarioGUI) {
        this.consultaUsuarioGUI = consultaUsuarioGUI;
    }

    public void inserir(UsuarioVO vo) throws Exception {
        usuarioService.inserir(vo);
    }
    
    public void alterar(UsuarioVO vo) throws Exception {
        usuarioService.alterar(vo);
    }
    
    public void consultar(UsuarioVO vo) throws Exception {
        this.usuarioVO = usuarioService.consultar(vo);
        consultaUsuarioGUI.consultar();
    }
    
    public List<UsuarioVO> getUsuarios() {
        return usuarioVO;
    }
}
