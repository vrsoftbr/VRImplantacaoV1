package vrimplantacao2.dao.cadastro.financeiro.verbas;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas.VerbasAnteriorVO;

/**
 *
 * @author Wesley
 */
public class VerbasProvider {

    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final VerbasDAO verbasDAO;
    private final VerbasAnteriorDAO verbasAnteriorDAO;
    private final FornecedorAnteriorDAO fornecedorAnteriorDAO;

    public VerbasProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.verbasDAO = new VerbasDAO();
        this.verbasAnteriorDAO = new VerbasAnteriorDAO();
        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
        this.verbasAnteriorDAO.createTable();
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

    public Map<String, VerbasAnteriorVO> getAnteriores() throws Exception {
        return this.verbasAnteriorDAO.getAnteriores(getSistema(), getLoja());
    }
    
    public MultiMap<String, FornecedorAnteriorVO> getFornecedoresAnteriores() throws Exception {
        return this.fornecedorAnteriorDAO.getAnteriores();
    }
    
    public void gravarVerbas(VerbasVO verbas) throws Exception {
        this.verbasDAO.gravarVerbas(verbas);
    }

    public void gravarVerbasAnterior(VerbasAnteriorVO anterior) throws Exception {
        this.verbasAnteriorDAO.gravarVerbasAnterior(anterior);
    }
    
    public void gravarVencimento(VerbasVO verbas) throws Exception {
        this.verbasDAO.gravarVencimento(verbas);
    }
}
