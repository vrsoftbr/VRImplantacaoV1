package vrimplantacao2.dao.cadastro.cheque;

import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.diversos.BancoDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.cheque.ChequeVO;

/**
 *
 * @author Leandro
 */
public class ChequeRepositoryProvider {
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private ChequeDAO chequeDAO;
    private ChequeAnteriorDAO anteriorDAO;
    private BancoDAO bancoDAO;

    public ChequeRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;        
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.bancoDAO = new BancoDAO();
        this.chequeDAO = new ChequeDAO();
        this.anteriorDAO = new ChequeAnteriorDAO();
        this.anteriorDAO.createTable();        
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

    public MultiMap<String, ChequeAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    public void gravarCheque(ChequeVO ch) throws Exception {
        chequeDAO.gravarCheque(ch);
    }

    public void gravarChequeAnterior(ChequeAnteriorVO anterior) throws Exception {
        anteriorDAO.gravarChequeAnterior(anterior);
    }

    public Set<Integer> getBancosExistentes() throws Exception {
        return bancoDAO.getBancosExistentes();
    }
    
}
