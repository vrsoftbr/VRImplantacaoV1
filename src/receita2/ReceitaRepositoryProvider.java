package receita2;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.receita.ReceitaAnteriorDAO;
import vrimplantacao2.dao.cadastro.receita.ReceitaDAO;
import vrimplantacao2.vo.cadastro.receita.ReceitaAnteriorVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaItemVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaProdutoVO;
import vrimplantacao2.vo.cadastro.receita.ReceitaVO;

/**
 *
 * @author leandro
 */
class ReceitaRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ReceitaAnteriorDAO anteriorDAO;
    private final ReceitaDAO receitaDAO;
    private final ProdutoAnteriorDAO produtoAnteriorDao;

    ReceitaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.anteriorDAO = new ReceitaAnteriorDAO();
        this.receitaDAO = new ReceitaDAO();
        this.produtoAnteriorDao = new ProdutoAnteriorDAO();
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
    
    void setMessage(String message) throws Exception {
        ProgressBar.setStatus(message);
    }
    
    void setMessage(String message, int size) throws Exception {
        ProgressBar.setStatus(message);
        ProgressBar.setMaximum(size);
    }
    
    void setMessage() throws Exception {
        ProgressBar.next();
    }
    
    void begin() throws Exception {
        Conexao.begin();
    }
    
    void commit() throws Exception {
        Conexao.commit();
    }
    
    void rollback() throws Exception {
        Conexao.rollback();
    }

    Map<String, ReceitaAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(sistema, loja);
    }

    void inserirItem(ReceitaItemVO i) throws Exception {
        receitaDAO.gravarItem(i);
    }

    void log(Logger LOG, Level level, String message, Object... params) {
        String format = String.format(message, params);
        LOG.log(level, format);
        System.out.println(format);
    }

    Map<String, Integer> getProdutos() throws Exception {
        return produtoAnteriorDao.getAnteriores(sistema, loja);
    }

    void inserirReceitaProduto(ReceitaProdutoVO i) throws Exception {
        receitaDAO.gravarProduto(i);
    }

    void incluirReceita(ReceitaVO receita) throws Exception {
        receitaDAO.gravar(receita);
    }

    void incluirAnterior(ReceitaAnteriorVO anterior) throws Exception {
        anteriorDAO.gravar(anterior);
    }

    void atualizarAnterior(ReceitaAnteriorVO anterior) throws Exception {
        anteriorDAO.atualizar(anterior);
    }
    
}
