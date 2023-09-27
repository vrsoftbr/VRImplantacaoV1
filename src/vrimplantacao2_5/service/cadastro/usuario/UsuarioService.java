package vrimplantacao2_5.service.cadastro.usuario;

import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.cadastro.usuario.UsuarioDAO;
import vrimplantacao2_5.provider.ConexaoProvider;
import vrimplantacao2_5.vo.cadastro.UsuarioVO;

/**
 *
 * @author Desenvolvimento
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final ConexaoProvider provider;
    
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.provider = new ConexaoProvider();
    }
    
    public UsuarioService(UsuarioDAO usuarioDAO,
                          ConexaoProvider provider) {
        this.usuarioDAO = usuarioDAO;
        this.provider = provider;
    }

    public void existeUsuario(UsuarioVO vo) throws Exception {
        if (usuarioDAO.existeUsuario(vo)) {
            throw new VRException("Usuario já cadastro.");
        }
    }
    
    public void inserir(UsuarioVO vo) throws Exception {
        
        provider.begin();
        
        try {
            
            existeUsuario(vo);
            usuarioDAO.inserir(vo);
            
            provider.commit();
            
        } catch (Exception ex) {
            provider.rollback();
            Util.exibirMensagemErro(ex, getTitle());
        }
    }
    
    public void alterar(UsuarioVO vo) throws Exception {
        
        provider.begin();
        
        try {
            
            usuarioDAO.alterar(vo);
            
            provider.commit();
            
        } catch (Exception ex) {
            provider.rollback();
            Util.exibirMensagemErro(ex, getTitle());
        }
    }
    
    public List<UsuarioVO> consultar(UsuarioVO vo) throws Exception {
        return usuarioDAO.consultar(vo);
    }
    
    public List<UsuarioVO> getUsuario() throws Exception {
        return usuarioDAO.getUsuario();
    }
    
    private String getTitle() {
        return "Cadastro Usuário";
    }        

    public List<UsuarioVO> autenticar(UsuarioVO vo) throws Exception {
        List<UsuarioVO> result = null;
        
        result = usuarioDAO.autenticar(vo);
        
        for (UsuarioVO usuario : result) {
            if (BCrypt.checkpw(vo.getSenha(), usuario.getSenha())){
                return result;
            }
        }

        result.clear();
        
        if (result == null || result.isEmpty()) {
            throw new VRException("Usuário não existe ou senha está incorreta.");
        }
        
        return null;
    }
    
    public int getProximoId() throws Exception {
        return usuarioDAO.getProximoId();
    }
}
