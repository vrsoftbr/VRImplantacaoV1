package vrimplantacao2.dao.cadastro.comprador;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.vo.cadastro.comprador.CompradorAnteriorVO;
import vrimplantacao2.vo.cadastro.comprador.CompradorVO;

/**
 *
 * @author Leandro
 */
public class CompradorRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final CompradorDAO dao;
    private final CompradorAnteriorDAO anteriorDAO;

    public CompradorRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.dao = new CompradorDAO();
        this.anteriorDAO = new CompradorAnteriorDAO();
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void setMessage(String message) throws Exception {
        ProgressBar.setStatus(message);
    }

    public void setMessage(String message, int size) throws Exception {
        setMessage(message);
        ProgressBar.setMaximum(size);
    }
    
    public void setMessage() throws Exception {
        ProgressBar.next();
    }

    public Map<String, CompradorAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(sistema, loja);
    }

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public void gravar(CompradorVO vo) throws Exception {
        dao.gravar(vo);
    }

    public void gravar(CompradorAnteriorVO anterior) throws Exception {
        anteriorDAO.gravar(anterior);
    }    

    public IDStack getIDsVagos() throws Exception {
        return dao.getIdsVagos(99999);
    }
    
}
