package vrimplantacao2.dao.cadastro.receita;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaFilizolaVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaBalancaToledoVO;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaRepositoryProvider {
 
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ProdutoAnteriorDAO produtoDAO;
    private final ReceitaBalancaAnteriorDAO anteriorDAO;
    private final ReceitaBalancaFilizolaDAO receitaBalancaFilizolaDAO;
    private final ReceitaBalancaToledoDAO receitaBalancaToledoDAO;

    public ReceitaBalancaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.produtoDAO = new ProdutoAnteriorDAO();
        this.anteriorDAO = new ReceitaBalancaAnteriorDAO();
        this.receitaBalancaFilizolaDAO = new ReceitaBalancaFilizolaDAO();
        this.receitaBalancaToledoDAO = new ReceitaBalancaToledoDAO();
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

    public Map<String, Integer> getProdutos() throws Exception {
        return produtoDAO.getAnteriores(sistema, loja);
    }

    public Map<String, ReceitaBalancaAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(sistema, loja);
    }

    public IDStack getIdsVagosFilizola() throws Exception {
        return receitaBalancaFilizolaDAO.getIdsVagos(99999);
    }

    public IDStack getIdsVagosToledo() throws Exception {
        return receitaBalancaToledoDAO.getIdsVagos(99999);
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

    public void gravar(ReceitaBalancaFilizolaVO vo) throws Exception {
        receitaBalancaFilizolaDAO.gravar(vo);
    }

    public void gravar(ReceitaBalancaToledoVO vo) throws Exception {
        receitaBalancaToledoDAO.gravar(vo);
    }
    
    public void gravar(ReceitaBalancaAnteriorVO vo) throws Exception {
        anteriorDAO.gravar(vo);
    }

    public void gravarItemFilizola(int idReceita, int idProduto) throws Exception {
        receitaBalancaFilizolaDAO.gravarItem(idReceita, idProduto);
    }

    public void gravarItemToledo(int idReceita, int idProduto) throws Exception {
        receitaBalancaToledoDAO.gravarItem(idReceita, idProduto);
    }

    public MultiMap<Integer, Void> getReceitasFilizola() throws Exception {
        return receitaBalancaFilizolaDAO.getReceitas();
    }

    public MultiMap<Integer, Void> getReceitasToledo() throws Exception {
        return receitaBalancaToledoDAO.getReceitas();
    }
    
}
