package vrimplantacao2.dao.cadastro.notafiscal;

import vrimplantacao2.dao.cadastro.cliente.ClienteEventualAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;

/**
 * Provedor de dados para a classe {@link NotaFiscalRepository}.
 * @author Leandro
 */
public class NotaFiscalRepositoryProvider {
    
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    
    private NotaEntradaDAO notaEntradaDAO;
    private NotaSaidaDAO notaSaidaDAO;
    private FornecedorAnteriorDAO fornecedorAnteriorDAO;
    private ClienteEventualAnteriorDAO clienteEventualAnteriorDAO;

    public NotaFiscalRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        
        this.notaEntradaDAO = new NotaEntradaDAO();
        this.notaSaidaDAO = new NotaSaidaDAO();
        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
        this.clienteEventualAnteriorDAO = new ClienteEventualAnteriorDAO();
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

    public int getTipoNotaEntrada() throws Exception {
        return notaEntradaDAO.getTipoNotaEntrada();
    }

    public int getTipoNotaSaida() throws Exception {
        return notaSaidaDAO.getTipoNotaSaida();
    }

    public Integer getFornecedorById(String id) throws Exception {
        return fornecedorAnteriorDAO.getByIdAnterior(getSistema(), getLojaOrigem(), id);
    }

    public Integer getClienteEventual(String id) throws Exception {
        return clienteEventualAnteriorDAO.getByIdAnterior(getSistema(), getLojaOrigem(), id);
    }
    
}
