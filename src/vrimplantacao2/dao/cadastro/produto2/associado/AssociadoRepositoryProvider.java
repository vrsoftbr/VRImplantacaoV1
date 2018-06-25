package vrimplantacao2.dao.cadastro.produto2.associado;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoVO;

/**
 *
 * @author Leandro
 */
public class AssociadoRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ProdutoAnteriorDAO produtoDAO = new ProdutoAnteriorDAO();
    private final AssociadoDAO associadoDAO = new AssociadoDAO();

    public AssociadoRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
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

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void setStatus(String mensagem, int size) throws Exception {
        setStatus(mensagem);
        ProgressBar.setMaximum(size);
    }
    
    public void setStatus() throws Exception {
        ProgressBar.next();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public Map<String, Integer> getProdutosAnteriores() throws Exception {
        return produtoDAO.getAnteriores(getSistema(), getLoja());
    }

    public Map<Integer, AssociadoVO> getAssociadosExistentes() throws Exception {
        return associadoDAO.getAssociadosExistentes();
    }

    public void gravar(AssociadoVO vo) throws Exception {
        associadoDAO.gravar(vo);
    }

    public void gravar(AssociadoItemVO vItem) throws Exception {
        associadoDAO.gravar(vItem);
    }
    
}
