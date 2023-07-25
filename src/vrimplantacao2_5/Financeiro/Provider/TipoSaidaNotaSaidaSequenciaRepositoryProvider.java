package vrimplantacao2_5.Financeiro.Provider;

import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2_5.Financeiro.DAO.TipoSaidaNotaSaidaSequenciaDAO;
import vrimplantacao2_5.Financeiro.VO.TipoSaidaNotaFiscalSequenciaVO;

public class TipoSaidaNotaSaidaSequenciaRepositoryProvider {

    private final TipoSaidaNotaSaidaSequenciaDAO tipoSaidaSeqSaida = new TipoSaidaNotaSaidaSequenciaDAO();

    private final String sistema;
    private final String loja;
    private final int lojaVR;

    public TipoSaidaNotaSaidaSequenciaRepositoryProvider(String sistema, String loja, int lojaVR) {
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

    public void gravar(TipoSaidaNotaFiscalSequenciaVO vo) throws Exception {
        tipoSaidaSeqSaida.salvar(vo);

    }

}
