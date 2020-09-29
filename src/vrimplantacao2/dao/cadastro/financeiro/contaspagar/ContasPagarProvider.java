package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.util.Date;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.RepositoryProvider;
import vrimplantacao2.dao.cadastro.diversos.BancoDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorParcelaVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVencimentoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;

/**
 *
 * @author Leandro
 */
public class ContasPagarProvider implements RepositoryProvider {
    private String sistema;
    private String agrupador;
    private int lojaVR;
    private FornecedorDAO fornecDAO;
    private FornecedorAnteriorDAO fornecedorDAO;
    private ContaPagarAnteriorDAO anteriorDAO;
    private PagarOutrasDespesasDAO despesaDAO;
    private PagarOutrasDespesasVencimentoDAO vencimentoDAO;
    private PagarFornecedorDAO pagarFornecedorDAO;
    private PagarFornecedorParcelaDAO pagarFornecedorParcelaDAO;
    private BancoDAO bancoDAO;

    public ContasPagarProvider(String sistema, String agrupador, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.agrupador = agrupador;
        this.lojaVR = lojaVR;
        this.fornecDAO = new FornecedorDAO();
        this.fornecedorDAO = new FornecedorAnteriorDAO();
        this.despesaDAO = new PagarOutrasDespesasDAO();
        this.vencimentoDAO = new PagarOutrasDespesasVencimentoDAO();
        this.anteriorDAO = new ContaPagarAnteriorDAO();
        this.pagarFornecedorDAO = new PagarFornecedorDAO();
        this.pagarFornecedorParcelaDAO = new PagarFornecedorParcelaDAO();
        this.bancoDAO = new BancoDAO();
        this.anteriorDAO.createTable();
    }

    @Override
    public String getSistema() {
        return this.sistema;
    }

    @Override
    public String getAgrupador() {
        return this.agrupador;
    }

    @Override
    public int getLojaVR() {
        return this.lojaVR;
    }

    public MultiMap<Long, FornecedorVO> getCnpjFornecedor() throws Exception {
        return this.fornecDAO.getCnpjExistente();
    }
            
    public MultiMap<String, FornecedorAnteriorVO> getFornecedores() throws Exception {
        return this.fornecedorDAO.getAnteriores();
    }

    public MultiMap<String, ContaPagarAnteriorVO> getAnteriores() throws Exception {
        return this.anteriorDAO.getAnteriores(getSistema(), getAgrupador());
    }

    public MultiMap<String, PagarFornecedorVO> getPagarFornecedores(int idLoja, int idFornecedor, int numeroDocumento, Date dataemissao) throws Exception {
        return this.pagarFornecedorDAO.getPagarFornecedores(idLoja, idFornecedor, numeroDocumento, dataemissao);
    }

    public MultiMap<String, PagarFornecedorParcelaVO> getPagarFornecedoresParcela(int idPagarFornecedor, int numeroParcela) throws Exception {
        return this.pagarFornecedorDAO.getPagarFornecedoresParcela(idPagarFornecedor, numeroParcela);
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

    public void atualizarAnterior(ContaPagarAnteriorVO anterior) throws Exception {
        this.anteriorDAO.atualizar(anterior);
    }

    public void gravarAnterior(ContaPagarAnteriorVO anterior) throws Exception {
        this.anteriorDAO.gravar(anterior);
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

    public void gravar(PagarOutrasDespesasVO vo) throws Exception {
        this.despesaDAO.gravar(vo);
    }

    public void atualizar(PagarOutrasDespesasVO vo, Set<OpcaoContaPagar> opt) throws Exception {
        this.despesaDAO.atualizar(vo, opt);
    }

    public void gravarVencimento(PagarOutrasDespesasVencimentoVO vc) throws Exception {
        this.vencimentoDAO.gravar(vc);
    }

    public MultiMap<String, Void> getPagamentos(boolean outrasDespesas) throws Exception {
        if (outrasDespesas) {
            return this.vencimentoDAO.getPagamentos();
        } else {
            return this.pagarFornecedorParcelaDAO.getPagamentos();
        }
    }

    public int getFornecedorLoja() throws Exception {
        return this.despesaDAO.getFornecedorLoja(lojaVR);
    }

    public void gravar(PagarFornecedorVO vo) throws Exception {
        this.pagarFornecedorDAO.gravar(vo);
    }    

    public void gravarVencimento(PagarFornecedorParcelaVO parc) throws Exception {
        this.pagarFornecedorParcelaDAO.gravarPagarFornecedorParcela(parc);
    }
    
    public Set<Integer> getBancosExistentes() throws Exception {
        return bancoDAO.getBancosExistentes();
    }
    
}
