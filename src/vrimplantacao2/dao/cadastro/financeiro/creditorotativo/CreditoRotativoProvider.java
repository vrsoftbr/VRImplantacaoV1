package vrimplantacao2.dao.cadastro.financeiro.creditorotativo;

import java.util.Date;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoItemVO;
import vrimplantacao2.vo.cadastro.cliente.rotativo.CreditoRotativoVO;

/**
 *
 * @author Leandro
 */
public class CreditoRotativoProvider {
    
    private String sistema;
    private String loja;
    private int lojaVR;
    private CreditoRotativoDAO rotativoDAO;
    private CreditoRotativoAnteriorDAO rotativoAnteriorDAO;
    private CreditoRotativoItemDAO rotativoItemDAO;
    private CreditoRotativoItemAnteriorDAO rotativoItemAnteriorDAO;
    private ClientePreferencialAnteriorDAO clienteAnteriorDAO;

    public CreditoRotativoProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.rotativoDAO = new CreditoRotativoDAO();
        this.rotativoAnteriorDAO = new CreditoRotativoAnteriorDAO();
        this.rotativoAnteriorDAO.createTable();
        this.rotativoItemDAO = new CreditoRotativoItemDAO();
        this.rotativoItemAnteriorDAO = new CreditoRotativoItemAnteriorDAO();
        this.rotativoItemAnteriorDAO.createTable();
        this.clienteAnteriorDAO = new ClientePreferencialAnteriorDAO();
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

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
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

    public Map<String, CreditoRotativoAnteriorVO> getAnteriores() throws Exception {
        return this.rotativoAnteriorDAO.getAnteriores(getSistema(), getLoja());
    }
    
    public Map<String, CreditoRotativoAnteriorVO> getTodoCreditoRotativoAnterior() throws Exception {
        return this.rotativoAnteriorDAO.getTodoCreditoRotativoAnterior();
    }

    public MultiMap<String, ClientePreferencialAnteriorVO> getClientesAnteriores() throws Exception {
        return this.clienteAnteriorDAO.getAnteriores(getSistema(), getLoja());
    }

    public MultiMap<String, CreditoRotativoItemAnteriorVO> getBaixasAnteriores() throws Exception {
        return this.rotativoItemAnteriorDAO.getBaixasAnteriores(getSistema(), getLoja());
    }
    
    public MultiMap<String, CreditoRotativoItemAnteriorVO> getTodaBaixaAnterior() throws Exception {
        return this.rotativoItemAnteriorDAO.getBaixasAnteriores(null, null);
    }
    
    public MultiMap<String, CreditoRotativoVO> getCreditoRotativo(int idLoja, int idClientePreferencial, int numeroCupom, Date dataEmissao) throws Exception {
        return this.rotativoDAO.getCreditoRotativo(idLoja, idClientePreferencial, numeroCupom, dataEmissao);
    }

    public void gravarRotativo(CreditoRotativoVO cred) throws Exception {
        this.rotativoDAO.gravarRotativo(cred);
    }

    public void gravarRotativoAnterior(CreditoRotativoAnteriorVO anterior) throws Exception {
        this.rotativoAnteriorDAO.gravarRotativoAnterior(anterior);
    }

    public void gravarRotativoItemAnterior(CreditoRotativoItemAnteriorVO parcAnt) throws Exception {
        this.rotativoItemAnteriorDAO.gravarRotativoItemAnterior(parcAnt);
    }

    public void gravarRotativoItem(CreditoRotativoItemVO item) throws Exception {
        this.rotativoItemDAO.gravarRotativoItem(item);
    }

    public void verificarBaixado(int id) throws Exception {
        this.rotativoDAO.verificarBaixado(id);
    }

    public void setStatus(String msg) throws Exception {
        ProgressBar.setStatus(msg);
    }

    public void setStatus(String msg, int size) throws Exception {
        setStatus(msg);
        ProgressBar.setMaximum(size);
    }

    public void setStatus() throws Exception {
        ProgressBar.next();
    }

    public Map<Integer, Double> getBaixas() throws Exception {
        return this.rotativoItemDAO.getBaixas();
    }
}
