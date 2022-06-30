package vrimplantacao2.dao.cadastro.nutricional;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.nutricional.NutricionalAnteriorVO;

/**
 *
 * @author Leandro
 */
public class NutricionalRepositoryProvider {
    
    private String sistema;
    private String loja;
    private int lojaVR;
    private FilizolaDAO filizolaDAO;
    private ToledoDAO toledoDAO;
    private NutricionalAnteriorDAO anteriorDAO;
    private ProdutoAnteriorDAO produtoDAO;

    public NutricionalRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        
        this.filizolaDAO = new FilizolaDAO();
        this.toledoDAO = new ToledoDAO();
        this.anteriorDAO = new NutricionalAnteriorDAO();
        this.produtoDAO = new ProdutoAnteriorDAO();
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
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
    
    public void gravar(NutricionalFilizolaVO vo) throws Exception {
        this.filizolaDAO.gravar(vo);
    }
    
    public void gravar(NutricionalToledoVO vo) throws Exception {
        this.toledoDAO.gravar(vo);
    }

    public Map<String, NutricionalAnteriorVO> getAnteriores() throws Exception {
        return this.anteriorDAO.getAnteriores(sistema, loja);
    }

    public Map<String, Integer> getProdutos() throws Exception {
        return this.produtoDAO.getAnteriores(sistema, loja);
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

    public void gravar(NutricionalAnteriorVO anterior) throws Exception {
        this.anteriorDAO.gravar(anterior);
    }

    public MultiMap<Integer, Void> getNutricionaisFilizola() throws Exception {
        return this.filizolaDAO.getNutricionais();
    }
    
    public MultiMap<Integer, Void> getNutricionaisToledo() throws Exception {
        return this.toledoDAO.getNutricionais();
    }

    public void gravarItemFilizola(Integer idNutricional, Integer idProduto) throws Exception {
        this.filizolaDAO.gravarItem(idNutricional, idProduto);
    }
    
    public void gravarItemToledo(Integer idNutricional, Integer idProduto) throws Exception {
        this.toledoDAO.gravarItem(idNutricional, idProduto);
    }

    public IDStack getIdsVagosFilizola() throws Exception {
        return this.filizolaDAO.getIdsVagos(999999);
    }

    public IDStack getIdsVagosToledo() throws Exception {
        return this.toledoDAO.getIdsVagos(999999);
    }
}