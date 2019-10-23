package vrimplantacao2.dao.cadastro.notafiscal;

import java.util.Map;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.AliquotaDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao2.dao.cadastro.cliente.ClienteEventualAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.PisCofinsDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoComplementoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntradaItem;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaidaItem;
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
    private ProdutoAnteriorDAO produtoAnteriorDAO;
    private AliquotaDAO aliquotaDAO;
    private ProdutoComplementoDAO produtoComplementoDAO;
    private PisCofinsDAO pisCofinsDAO;
    private MapaTributacaoDAO mapaTributacaoDAO;
    private FornecedorAnteriorDAO fornedorAnteriorDAO;

    public NotaFiscalRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        
        this.notaEntradaDAO = new NotaEntradaDAO();
        this.notaSaidaDAO = new NotaSaidaDAO();
        this.fornecedorAnteriorDAO = fornedorAnteriorDAO;
        this.clienteEventualAnteriorDAO = new ClienteEventualAnteriorDAO();
        this.notaFiscalAnteriorDAO = new NotaFiscalAnteriorDAO();
        this.produtoAnteriorDAO = new ProdutoAnteriorDAO();
        this.aliquotaDAO = new AliquotaDAO();
        this.produtoComplementoDAO = new ProdutoComplementoDAO();
        this.pisCofinsDAO = new PisCofinsDAO();
        this.mapaTributacaoDAO = new MapaTributacaoDAO();
        this.mapaTributacaoDAO.createTable();
        this.fornedorAnteriorDAO = new FornecedorAnteriorDAO();
        
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

    public void salvarEntrada(NotaEntrada ne) throws Exception {
        notaEntradaDAO.salvar(ne);
    }

    public void incluirAnterior(NotaFiscalAnteriorVO anterior) throws Exception {
        notaFiscalAnteriorDAO.incluir(anterior);
    }

    public void atualizarAnterior(NotaFiscalAnteriorVO anterior) throws Exception {
        notaFiscalAnteriorDAO.atualizar(anterior);
    }

    public void eliminarItensNotaEntrada(int codigoAtual) throws Exception {
        notaEntradaDAO.eliminarItens(codigoAtual);
    }
    
    public void eliminarNotaEntrada(int codigoAtual) throws Exception {
        notaEntradaDAO.eliminarNota(codigoAtual);
    }

    public void eliminarItensNotaSaida(int codigoAtual) throws Exception {
        notaSaidaDAO.eliminarItens(codigoAtual);
    }

    public void eliminarNotaSaida(int codigoAtual) throws Exception {
        notaSaidaDAO.eliminarNota(codigoAtual);
    }

    public Integer getIdNotaEntrada(NotaFiscalIMP imp, int idFornecedor) throws Exception {
        return notaEntradaDAO.getNota(imp, idFornecedor, getLojaVR());
    }

    public Integer getIdNotaSaida(NotaFiscalIMP imp) throws Exception {
        return notaSaidaDAO.getNota(imp, getLojaVR());
    }

    public void salvarSaida(NotaSaida ns) throws Exception {
        notaSaidaDAO.salvar(ns);
    }
    
    public void salvarEntradaItem(NotaEntradaItem item) throws Exception {
        notaEntradaDAO.salvarItem(item);
    }

    public void salvarSaidaItem(NotaSaidaItem item) throws Exception  {
        notaSaidaDAO.salvarItem(item);
    }

    public Map<String, Integer> getProdutosAnteriores() throws Exception {
        return produtoAnteriorDAO.getAnteriores(sistema, lojaOrigem);
    }

    public Map<String, Integer> getAliquotaPorId() throws Exception {
        return mapaTributacaoDAO.getAliquotaPorId(sistema, lojaOrigem);
    }

    public Map<String, Integer> getAliquotaPorValor() throws Exception {
        return aliquotaDAO.getAliquotaPorValor();
    }

    public Map<Integer, Double> getCustoProduto() throws Exception {
        return produtoComplementoDAO.getCustoProduto(lojaVR);
    }

    public Map<Integer, Integer> getPisCofins() throws Exception {
        return pisCofinsDAO.getPisCofinsByCst();
    }

    public int getIdFornecedorLoja() throws Exception {
        return new LojaDAO().getIdFornecedor(lojaVR);
    }

    public void atualizarSaida(NotaSaida ns) {
        this.notaSaidaDAO.atualizar(ns);
    }

    public void atualizarEntrada(NotaEntrada ne) {
        this.notaEntradaDAO.atualizar(ne);
    }

    public Map<String, Integer> getFornecedores() throws Exception {
        return fornedorAnteriorDAO.getFornecedoresImportados(sistema, lojaOrigem);
    }

    public Map<String, Integer> getClientesEventuais() throws Exception {
        return clienteEventualAnteriorDAO.getClientesEventuaisImportados(sistema, lojaOrigem);
    }
    
}
