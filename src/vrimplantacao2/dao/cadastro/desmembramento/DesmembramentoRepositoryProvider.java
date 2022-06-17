package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoItemVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;

public class DesmembramentoRepositoryProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final ProdutoAnteriorDAO produtoDAO = new ProdutoAnteriorDAO();
    private final DesmembramentoDAO desmembramentoDAO = new DesmembramentoDAO();

    public DesmembramentoRepositoryProvider(String sistema, String loja, int lojaVR) {
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

    public void next() throws Exception {
        ProgressBar.next();
    }

    public void setMaximo(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public Map<String, Integer> getProdutosAnteriores() throws Exception {
        return produtoDAO.getAnteriores(getSistema(), getLoja());
    }

    public Map<Integer, DesmembramentoVO> getDesmembramentosExistentes() throws Exception {
        return desmembramentoDAO.getDesmembramentosExistentes();
    }

    public void gravar(DesmembramentoVO vo) throws Exception {
        desmembramentoDAO.gravar(vo);
    }

    public void gravar(DesmembramentoItemVO vItem) throws Exception {
        desmembramentoDAO.gravar(vItem);
    }

    public Set<Integer> getProdutosAtivos() throws Exception {
        return desmembramentoDAO.getProdutosAtivos(getLojaVR());
    }
}
