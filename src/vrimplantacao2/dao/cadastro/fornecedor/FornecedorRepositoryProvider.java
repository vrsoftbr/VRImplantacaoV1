package vrimplantacao2.dao.cadastro.fornecedor;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.local.MunicipioDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FamiliaFornecedorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorPagamentoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.cadastro.fornecedor.ProdutoFornecedorVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;

/**
 *
 * @author Leandro
 */
public class FornecedorRepositoryProvider {

    private int idConexao = 0;
    private String sistema;
    private String lojaOrigem;
    private int lojaVR;
    private MultiMap<String, FamiliaFornecedorVO> familias;
    private FornecedorDAO fornecedorDAO;
    private FornecedorEnderecoDAO fornecedorEnderecoDAO;
    private ProdutoFornecedorDAO produtoFornecedorDAO;
    private FornecedorAnteriorDAO anterioresDAO;
    private MunicipioDAO municipioDAO;
    private FornecedorContatoDAO fornecedorContatoDAO;
    private FornecedorPagamentoDAO pagamentoDAO;
    private FornecedorPrazoDAO fornecedorPrazoDAO;
    private FornecedorPagamentoDAO fornecedorPagamenDAO;
    private FornecedorPrazoPedidoDAO fornecedorPrazoPedido;
    private Set<OpcaoFornecedor> opcoes;

    public FornecedorRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.fornecedorDAO = new FornecedorDAO();
        this.fornecedorEnderecoDAO = new FornecedorEnderecoDAO();
        this.produtoFornecedorDAO = new ProdutoFornecedorDAO();
        this.fornecedorContatoDAO = new FornecedorContatoDAO();
        this.anterioresDAO = new FornecedorAnteriorDAO();
        this.municipioDAO = new MunicipioDAO();
        this.pagamentoDAO = new FornecedorPagamentoDAO();
        this.fornecedorPrazoDAO = new FornecedorPrazoDAO();
        this.fornecedorPagamenDAO = new FornecedorPagamentoDAO();
        this.fornecedorPrazoPedido = new FornecedorPrazoPedidoDAO();
    }

    public FornecedorRepositoryProvider(String sistema, String lojaOrigem, int lojaVR, int idConexao) throws Exception {
        this.idConexao = idConexao;
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.fornecedorDAO = new FornecedorDAO();
        this.fornecedorEnderecoDAO = new FornecedorEnderecoDAO();
        this.produtoFornecedorDAO = new ProdutoFornecedorDAO();
        this.fornecedorContatoDAO = new FornecedorContatoDAO();
        this.anterioresDAO = new FornecedorAnteriorDAO();
        this.municipioDAO = new MunicipioDAO();
        this.pagamentoDAO = new FornecedorPagamentoDAO();
        this.fornecedorPrazoDAO = new FornecedorPrazoDAO();
        this.fornecedorPagamenDAO = new FornecedorPagamentoDAO();
        this.fornecedorPrazoPedido = new FornecedorPrazoPedidoDAO();
    }

    public int getIdConexao() {
        return this.idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }

    public void setOpcoes(Set<OpcaoFornecedor> opcoes) {
        this.opcoes = opcoes;
    }

    public Set<OpcaoFornecedor> getOpcoes() {
        return opcoes;
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

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void setStatus(String mensagem) throws Exception {
        ProgressBar.setStatus(mensagem);
    }

    public void setMaximum(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void next() throws Exception {
        ProgressBar.next();
    }

    public MultiMap<String, FornecedorAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnteriores();
    }

    public MunicipioVO getMunicipio(int ibge_municipio) throws Exception {
        return municipioDAO.getMunicipio(ibge_municipio);
    }

    public MunicipioVO getMunicipio(String municipio, String uf) throws Exception {
        return municipioDAO.getMunicipio(municipio, uf);
    }

    public MunicipioVO getMunicipioPadrao() throws Exception {
        return Parametros.get().getMunicipioPadrao2();
    }

    public Map<Long, FornecedorVO> getCnpjExistentes() throws Exception {
        return fornecedorDAO.getCnpjExistentes();
    }

    public void gravarFornecedor(FornecedorVO vo) throws Exception {
        fornecedorDAO.gravarFornecedor(vo);
    }

    public void gravarFornecedorAnterior(FornecedorAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarFornecedorAnterior(anterior);
    }

    public FornecedorIDStack getIdsExistentes() throws Exception {
        return new FornecedorIDStack();
    }

    public MultiMap<String, Integer> getContatos() throws Exception {
        return fornecedorContatoDAO.getContatos();
    }

    public MultiMap<String, Void> getPagamentos() throws Exception {
        return fornecedorPagamenDAO.getPagamentos();
    }

    public MultiMap<String, Void> getDivisoes() throws Exception {
        return fornecedorPrazoDAO.getDivisoes(getLojaVR());
    }

    public void gravarFornecedorContato(FornecedorContatoVO contato) throws Exception {
        fornecedorContatoDAO.salvar(contato);
    }

    public void gravarCondicaoPagamento(int id, int condicaoPagamento) throws Exception {
        pagamentoDAO.salvar(id, condicaoPagamento);
    }

    public void gravarCondicaoPagamento(FornecedorPagamentoVO pagamento) throws Exception {
        pagamentoDAO.salvar(pagamento);
    }

    public void gravarPrazoFornecedor(int id, int divisao, int prazoEntrega, int prazoVisita, int prazoSeguranca) throws Exception {
        fornecedorPrazoDAO.salvar(getLojaVR(), id, divisao, prazoEntrega, prazoVisita, prazoSeguranca);
    }

    public void gravarPrazoPedidoFornecedor(int idFornecedor, int prazoPedidoEntrega) throws Exception {
        fornecedorPrazoPedido.salvarTodasLojas(idFornecedor, prazoPedidoEntrega);
    }

    public void atualizarFornecedor(FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
        fornecedorDAO.atualizarFornecedor(vo, opt);
    }

    public void atualizarProdutoFornecedor(ProdutoFornecedorVO vo, Set<OpcaoProdutoFornecedor> opt) throws Exception {
        produtoFornecedorDAO.atualizarProdutoFornecedor(vo, opt);
    }

    public void resetCnpjCpf() throws Exception {
        fornecedorDAO.resetCnpjCpf(getSistema(), getLojaOrigem());
    }

    public void atualizarContato(FornecedorContatoVO contato, Set<OpcaoFornecedor> opt) throws Exception {
        fornecedorContatoDAO.atualizar(contato, opt);
    }

    void gravarFornecedorEndereco() {
        fornecedorEnderecoDAO.gravarFornecedorEndereco(getSistema(), getLojaOrigem());
    }

    void atualizarFornecedorEndereco() {
        fornecedorEnderecoDAO.atualizarFornecedorEndereco();
        fornecedorEnderecoDAO.atualizarFornecedorEndereco();
    }

    public FamiliaFornecedorVO getFamiliaFornecedor(Integer idFamiliaFornecedor) throws Exception {
        if (familias == null) {
            familias = new FamiliaFornecedorDAO().getAnteriores();
        }
        return familias.get(sistema, lojaOrigem, idFamiliaFornecedor.toString());
    }
}
