package vrimplantacao2.dao.cadastro.financeiro.devolucao;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.devolucao.DevolucaoAnteriorVO;

/**
 *
 * @author Wesley
 */
public class DevolucaoProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final DevolucaoDAO devolucaoDAO;
    private final DevolucaoAnteriorDAO devolucaoAnteriorDAO;
    private final FornecedorAnteriorDAO fornecedorAnteriorDAO;

    public DevolucaoProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.devolucaoDAO = new DevolucaoDAO();
        this.devolucaoAnteriorDAO = new DevolucaoAnteriorDAO();
        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
        this.devolucaoAnteriorDAO.createTable();
    }

    public String getSistema() {
        return this.sistema;
    }

    public String getLoja() {
        return this.loja;
    }

    public int getLojaVR() {
        return this.lojaVR;
    }

    public void setStatus(String msg, int size) throws Exception {
        setStatus(msg);
        ProgressBar.setMaximum(size);
    }

    public void setStatus(String msg) throws Exception {
        ProgressBar.setStatus(msg);
    }

    public void setStatus() throws Exception {
        ProgressBar.next();
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

    public Map<String, DevolucaoAnteriorVO> getAnteriores() throws Exception {
        return this.devolucaoAnteriorDAO.getAnteriores(getSistema(), getLoja());
    }
        
    public MultiMap<String, FornecedorAnteriorVO> getFornecedoresAnteriores() throws Exception {
        return this.fornecedorAnteriorDAO.getAnteriores();
    }
        
    public void gravarDevolucao(DevolucaoVO devolucao) throws Exception {
        this.devolucaoDAO.gravarDevolucao(devolucao);
    }

    public void gravarDevolucaoAnterior(DevolucaoAnteriorVO anterior) throws Exception {
        this.devolucaoAnteriorDAO.gravarDevolucaoAnterior(anterior);
    }
}
