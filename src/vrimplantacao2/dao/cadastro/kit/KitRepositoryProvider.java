package vrimplantacao2.dao.cadastro.kit;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.kit.KitVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.kit.KitItemVO;
import vrimplantacao2.vo.cadastro.kit.KitAnteriorVO;
import vrimplantacao2.vo.cadastro.kit.KitItemAnteriorVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;

/**
 *
 * @author Wesley
 */
public class KitRepositoryProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final KitDAO kitDAO;
    private final ProdutoAnteriorDAO produtoDAO;
    private final KitAnteriorDAO anterioresDAO;
    private final KitItemAnteriorDAO itemAnteriorDAO;

    public KitRepositoryProvider(String sistema, String loja, int lojaVR) {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.kitDAO = new KitDAO();
        this.produtoDAO = new ProdutoAnteriorDAO();
        this.anterioresDAO = new KitAnteriorDAO();
        this.itemAnteriorDAO = new KitItemAnteriorDAO();
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
        kitDAO.createKitTable();
        kitDAO.createKitItemTable();
    }

    public Map<String, Integer> getProdutosAnteriores() throws Exception {
        return produtoDAO.getAnteriores(getSistema(), getLoja());
    }

    public void gravar(KitVO vo) throws Exception {
        kitDAO.gravar(vo);
    }

    public void gravar(KitItemVO vItem) throws Exception {
        kitDAO.gravar(vItem);
    }
    
    public void gravarKitLoja(KitVO vo) throws Exception {
        kitDAO.gravarKitLoja(vo);
    }

    public MultiMap<String, KitAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnteriores();
    }

    public MultiMap<String, KitItemAnteriorVO> getItemsAnteriores() throws Exception {
        return itemAnteriorDAO.getAnteriores();
    }

    public void gravarKitAnterior(KitAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarKitAnterior(anterior);
    }

    public void gravarKitItemAnterior(KitItemAnteriorVO anterior) throws Exception {
        itemAnteriorDAO.gravarKitItemAnterior(anterior);
    }
}
