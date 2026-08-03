package vrimplantacao2.dao.cadastro.produtosimilar;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarAnteriorVO;
import vrimplantacao2.vo.cadastro.produtosimilar.ProdutoSimilarItemAnteriorVO;

/**
 *
 * @author Wesley
 */
public class ProdutoSimilarRepositoryProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ProdutoSimilarDAO similarDAO;
    private final ProdutoAnteriorDAO produtoDAO;
    private final ProdutoSimilarAnteriorDAO anterioresDAO;
    private final ProdutoSimilarItemAnteriorDAO itemAnteriorDAO;

    public ProdutoSimilarRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.similarDAO = new ProdutoSimilarDAO();
        this.produtoDAO = new ProdutoAnteriorDAO();
        this.anterioresDAO = new ProdutoSimilarAnteriorDAO();
        this.itemAnteriorDAO = new ProdutoSimilarItemAnteriorDAO();
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
        similarDAO.createProdutoSimilarTable();
        similarDAO.createProdutoSimilarItemTable();
    }

    public Map<String, Integer> getProdutosAnteriores() throws Exception {
        return produtoDAO.getAnteriores(getSistema(), getLoja());
    }

    public void gravar(ProdutoSimilarVO vo) throws Exception {
        similarDAO.gravar(vo);
    }

    public void gravar(ProdutoSimilarItemVO vItem) throws Exception {
        similarDAO.gravar(vItem);
    }

    public MultiMap<String, ProdutoSimilarAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnteriores();
    }

    public MultiMap<String, ProdutoSimilarItemAnteriorVO> getItemsAnteriores() throws Exception {
        return itemAnteriorDAO.getAnteriores();
    }

    public void gravarProdutoSimilarAnterior(ProdutoSimilarAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarProdutoSimilarAnterior(anterior);
    }

    public void gravarProdutoSimilarItemAnterior(ProdutoSimilarItemAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarProdutoSimilarItemAnterior(anterior);
    }
}
