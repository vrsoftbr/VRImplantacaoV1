package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.produto2.TipoRecebivelDAO;
import vrimplantacao2.vo.cadastro.TipoRecebivelVO;

public class TipoRecebivelRepositoryProvider {

    private final TipoRecebivelDAO TipoRecebivelDAO = new TipoRecebivelDAO();

    private final String sistema;
    private final String loja;
    private final int lojaVR;

    public TipoRecebivelRepositoryProvider(String sistema, String loja, int lojaVR) {
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

    public void gravar(TipoRecebivelVO vo) throws Exception {
        TipoRecebivelDAO.salvar(vo);

    }

}
