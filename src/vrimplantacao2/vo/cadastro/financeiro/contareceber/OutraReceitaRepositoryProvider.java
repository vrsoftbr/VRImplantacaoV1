package vrimplantacao2.vo.cadastro.financeiro.contareceber;

import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.cliente.ClienteEventualAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;

/**
 * 
 * @author Leandro
 */
public class OutraReceitaRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final FornecedorAnteriorDAO fornecedorAnteriorDAO;
    private final ContaReceberAnteriorDAO anteriorDAO;
    private final OutraReceitaDAO outraReceitaDAO;
    private final OutraReceitaItemDAO outraReceitaItemDAO;
    private final ClienteEventualAnteriorDAO clienteEventualAnteriorDAO;
    private final ContaReceberItemAnteriorDAO contaReceberItemAnteriorDAO;

    public OutraReceitaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
        this.anteriorDAO = new ContaReceberAnteriorDAO();
        this.outraReceitaDAO = new OutraReceitaDAO();
        this.outraReceitaItemDAO = new OutraReceitaItemDAO();
        this.clienteEventualAnteriorDAO = new ClienteEventualAnteriorDAO();
        this.contaReceberItemAnteriorDAO = new ContaReceberItemAnteriorDAO();
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

    public void setStatus() throws Exception {
        ProgressBar.next();
    }

    public void setStatus(String message) throws Exception {
        ProgressBar.setStatus(message);
    }
    
    public void setStatus(String message, int size) throws Exception {
        ProgressBar.setStatus(message);
        ProgressBar.setMaximum(size);
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

    public Map<String, Integer> getFornecedores() throws Exception {
        return fornecedorAnteriorDAO.getFornecedoresImportados(sistema, loja);
    }

    public Map<String, ContaReceberAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(sistema, loja);
    }

    public void gravar(OutraReceitaVO vo) throws Exception {
        outraReceitaDAO.gravar(vo);
    }

    public void gravar(ContaReceberAnteriorVO anterior) throws Exception {
        anteriorDAO.gravar(anterior);
    }
    
    public void gravar(OutraReceitaItemVO item) throws Exception {
        outraReceitaItemDAO.gravar(item);
    }
    
    public void gravar(ContaReceberItemAnteriorVO ant) throws Exception {
        contaReceberItemAnteriorDAO.gravar(ant);
    }

    public Map<String, Integer> getEventuais() throws Exception {
        return clienteEventualAnteriorDAO.getClientesImportador(sistema, loja);
    }

    public MultiMap<String, ContaReceberItemAnteriorVO> getItemAnteriores() throws Exception {
        return contaReceberItemAnteriorDAO.getAnteriores(sistema, loja);
    }
    
}
