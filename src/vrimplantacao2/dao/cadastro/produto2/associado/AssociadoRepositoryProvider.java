package vrimplantacao2.dao.cadastro.produto2.associado;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoVO;
import vrimplantacao2.vo.cadastro.associado.AssociadoAnteriorVO;
import vrimplantacao2.dao.cadastro.associado.AssociadoAnteriorDAO;
import vrimplantacao2.vo.cadastro.associado.AssociadoItemAnteriorVO;
import vrimplantacao2.dao.cadastro.associado.AssociadoItemAnteriorDAO;

/**
 *
 * @author Leandro
 */
public class AssociadoRepositoryProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final AssociadoDAO associadoDAO;
    private final ProdutoAnteriorDAO produtoDAO;
    private final AssociadoAnteriorDAO anterioresDAO;
    private final AssociadoItemAnteriorDAO itemAnteriorDAO;

    public AssociadoRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.associadoDAO = new AssociadoDAO();
        this.produtoDAO = new ProdutoAnteriorDAO();
        this.anterioresDAO = new AssociadoAnteriorDAO();
        this.itemAnteriorDAO = new AssociadoItemAnteriorDAO();
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
    
    public void createTable() throws Exception {
        associadoDAO.createAssociadoTable();
        associadoDAO.createAssociadoItemTable();
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

    public Set<Integer> getProdutosAtivos() throws Exception {
        return associadoDAO.getProdutosAtivos(getLojaVR());
    }

    public MultiMap<String, AssociadoAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnteriores();
    }

    public MultiMap<String, AssociadoItemAnteriorVO> getItemsAnteriores() throws Exception {
        return itemAnteriorDAO.getAnteriores();
    }

    public void gravarAssociadoAnterior(AssociadoAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarAssociadoAnterior(anterior);
    }

    public void gravarAssociadoItemAnterior(AssociadoItemAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarAssociadoItemAnterior(anterior);
    }
}
