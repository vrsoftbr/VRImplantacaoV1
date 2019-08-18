package vrimplantacao2.vo.cadastro.divisao;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto2.DivisaoDAO;
import vrimplantacao2.utils.collection.IDStack;

/**
 *
 * @author Leandro
 */
public class DivisaoRepositoryProvider {
    
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private final DivisaoDAO divisaoDAO;

    public DivisaoRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.divisaoDAO = new DivisaoDAO();
    }

    public Map<String, Map.Entry<String, Integer>> getAnteriores() throws Exception {
        return divisaoDAO.getAnteriores(sistema, lojaOrigem);
    }

    public void notificar(String mensagem, int qtd) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(qtd);
    }
    
    public void notificar(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }
    
    public void notificar() throws Exception {
        ProgressBar.next();
    }

    public void salvar(DivisaoFornecedorVO vo) throws Exception {
        divisaoDAO.salvar(vo);
    }

    public void salvar(Map.Entry<String, Integer> anterior) throws Exception {
        divisaoDAO.salvar(sistema, lojaOrigem, anterior);
    }

    public IDStack getIdsVagos() throws Exception {
        return divisaoDAO.getIdsVagos();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void begin() throws Exception {
        Conexao.begin();
    }
    
}
