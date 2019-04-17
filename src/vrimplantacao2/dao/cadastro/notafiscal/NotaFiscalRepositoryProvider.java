package vrimplantacao2.dao.cadastro.notafiscal;

import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.cliente.ClienteEventualAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

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
    private NotaFiscalAnteriorDAO notaFiscalAnteriorDAO;

    public NotaFiscalRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        
        this.notaEntradaDAO = new NotaEntradaDAO();
        this.notaSaidaDAO = new NotaSaidaDAO();
        this.fornecedorAnteriorDAO = new FornecedorAnteriorDAO();
        this.clienteEventualAnteriorDAO = new ClienteEventualAnteriorDAO();
        this.notaFiscalAnteriorDAO = new NotaFiscalAnteriorDAO();
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

    //<editor-fold defaultstate="collapsed" desc="NOTIFICAÇÃO">
    public void notificar() throws Exception {
        ProgressBar.next();
    }
    
    public void notificar(String mensagem, int size) throws Exception {
        ProgressBar.setStatus(mensagem);
        ProgressBar.setMaximum(size);
    }
    
    public void notificar(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }
    //</editor-fold>

    public MultiMap<String, NotaFiscalAnteriorVO> getAnteriores() throws Exception {
        return notaFiscalAnteriorDAO.getAnteriores(getSistema(), getLojaOrigem());
    }

    void salvarEntrada(NotaEntrada ne) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void incluirAnterior(NotaFiscalAnteriorVO anterior) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void atualizarAnterior(NotaFiscalAnteriorVO anterior) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void salvarEntradaItens(NotaEntrada ne) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void eliminarNotaEntrada(int id) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void eliminarNotaSaida(int id) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    Integer getIdNotaEntrada(NotaFiscalIMP imp) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    Integer getIdNotaSaida(NotaFiscalIMP imp) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void salvarSaida(NotaSaida ns) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }

    void salvarSaidaItens(NotaSaida ns) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
}
