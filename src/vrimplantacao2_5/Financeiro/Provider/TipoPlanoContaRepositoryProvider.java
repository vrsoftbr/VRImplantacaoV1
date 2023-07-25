package vrimplantacao2_5.Financeiro.Provider;

import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2_5.Financeiro.DAO.TipoPlanoContaDAO;
import vrimplantacao2_5.Financeiro.VO.TipoPlanoContaVO;

public class TipoPlanoContaRepositoryProvider {

    private final TipoPlanoContaDAO planoConta = new TipoPlanoContaDAO();

    private final String sistema;
    private final String loja;
    private final int lojaVR;

    public TipoPlanoContaRepositoryProvider(String sistema, String loja, int lojaVR) {
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

    public void gravar(TipoPlanoContaVO vo) throws Exception {
        planoConta.salvar(vo);

    }

}
