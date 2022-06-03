package vrimplantacao2.dao.cadastro.desmembramento;

import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoAnteriorVO;
import vrimplantacao2.vo.cadastro.desmembramento.DesmembramentoVO;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;

public class DesmembramentoProvider {

    private final String lojaOrigem;
    private final int lojaVR;
    private final String sistema;
    private final int idConexao;
    private final DesmembramentoDAO desmembramentoDAO;
    private final DesmembramentoAnteriorDAO desmembramentoAnteriorDAO;

    public DesmembramentoProvider(String sistema, String lojaOrigem, int lojaVR, int idConexao) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.idConexao = idConexao;
        this.desmembramentoDAO = new DesmembramentoDAO();
        this.desmembramentoAnteriorDAO = new DesmembramentoAnteriorDAO();
        this.desmembramentoAnteriorDAO.createTable();
    }

    public void notificar(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void notificar(String mensagem, int size) throws Exception {
        notificar(mensagem);
        ProgressBar.setMaximum(size);
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }

    MultiMap<Long, DesmembramentoAnteriorDAO> getImpId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    MultiMap<Long, DesmembramentoVO> getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    MultiMap<String, DesmembramentoAnteriorVO> getAnteriores() throws Exception {
        return desmembramentoAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem(), getIdConexao());
    }

    public String getSistema() {
        return sistema;
    }

    public String getLojaOrigem() {
        return lojaOrigem;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public int getIdConexao() {
        return idConexao;
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

    public void next() throws Exception {
        ProgressBar.next();
    }

    public void setMaximo(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void gravarAnterior(DesmembramentoAnteriorVO anterior) throws Exception {
        desmembramentoAnteriorDAO.salvar(anterior);
    }
    
    public void gravarDesmembramento(DesmembramentoVO desmem) throws Exception {
        desmembramentoDAO.gravar(desmem);
    }

    public void gravarDesmembramentoAnterior(DesmembramentoAnteriorVO anterior) throws Exception {
        desmembramentoAnteriorDAO.salvar(anterior);
    }

    public void getDesmembramentoItens() throws Exception {
        List<DesmembramentoIMP> desmembramentoItens = desmembramentoDAO.getDesmembramentoItens();
    }

    public void gravarDesmembramentoItens(DesmembramentoAnteriorVO anterior) throws Exception {
        desmembramentoDAO.salvarDesmembramentoItens(anterior);
    }
}
