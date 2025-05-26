package vrimplantacao2.dao.cadastro.usuario;

import java.util.Set;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.usuario.UsuarioAnteriorVO;
import vrimplantacao2.vo.cadastro.usuario.UsuarioVO;
import vrimplantacao2.vo.cadastro.usuario.TipoSetorVO;

/**
 *
 * @author Wesley
 */
public class UsuarioRepositoryProvider {

    private final int idConexao;
    private final String sistema;
    private final String lojaOrigem;

    private final int lojaVR;
    private MultiMap<String, TipoSetorVO> setores;
    private UsuarioDAO usuarioDAO;
    private UsuarioAnteriorDAO anterioresDAO;
    private Set<OpcaoUsuario> opcoes;

    public UsuarioRepositoryProvider(String sistema, String lojaOrigem, int lojaVR, int idConexao) throws Exception {
        this.idConexao = idConexao;
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.usuarioDAO = new UsuarioDAO();
        this.anterioresDAO = new UsuarioAnteriorDAO();
    }

    public int getIdConexao() {
        return idConexao;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLojaOrigem() {
        return lojaOrigem;
    }

    public int getLojaVR() {
        return lojaVR;
    }
  
    public Set<OpcaoUsuario> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(Set<OpcaoUsuario> opcoes) {
        this.opcoes = opcoes;
    }

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }
        
    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void setMaximum(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void next() throws Exception {
        ProgressBar.next();
    }

    public MultiMap<String, UsuarioAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnteriores();
    }

    public MultiMap<String, UsuarioVO> getLoginExistentes() throws Exception {
        return usuarioDAO.getLoginExistentes();
    }

    public void gravarUsuario(UsuarioVO vo, Versao versao) throws Exception {
        usuarioDAO.gravarUsuario(vo, versao);
    }

    public void gravarUsuarioAnterior(UsuarioAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarUsuarioAnterior(anterior);
    }

    public UsuarioIDStack getIdsExistentes() throws Exception {
        return new UsuarioIDStack();
    }

    public TipoSetorVO getTipoSetor(Integer idTipoSetor) throws Exception {
        if (setores == null) {
            setores = new TipoSetorDAO().getAnteriores();
        }
        return setores.get(sistema, lojaOrigem, idTipoSetor.toString());
    }
}
