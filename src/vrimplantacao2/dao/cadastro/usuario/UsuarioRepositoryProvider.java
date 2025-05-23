package vrimplantacao2.dao.cadastro.usuario;

import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao2.dao.cadastro.local.MunicipioDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.fornecedor.FamiliaFornecedorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.usuario.UsuarioAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorPagamentoVO;
import vrimplantacao2.vo.cadastro.usuario.UsuarioVO;
import vrimplantacao2.vo.cadastro.fornecedor.ProdutoFornecedorVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;
import vrimplantacao2.vo.cadastro.usuario.TipoSetorVO;

/**
 *
 * @author Leandro
 */
public class UsuarioRepositoryProvider {

    private final int idConexao;
    private final String sistema;
    private final String lojaOrigem;

    private final int lojaVR;
    private MultiMap<String, TipoSetorVO> setores;
    private UsuarioDAO usuarioDAO;
//    private FornecedorEnderecoDAO fornecedorEnderecoDAO;
//    private ProdutoFornecedorDAO produtoFornecedorDAO;
    private UsuarioAnteriorDAO anterioresDAO;
//    private MunicipioDAO municipioDAO;
//    private FornecedorContatoDAO fornecedorContatoDAO;
//    private FornecedorPagamentoDAO pagamentoDAO;
//    private FornecedorPrazoDAO fornecedorPrazoDAO;
//    private FornecedorPagamentoDAO fornecedorPagamenDAO;
//    private FornecedorPrazoPedidoDAO fornecedorPrazoPedido;
    private Set<OpcaoUsuario> opcoes;

//    public UsuarioRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
//        this.sistema = sistema;
//        this.lojaOrigem = lojaOrigem;
//        this.lojaVR = lojaVR;
//        this.fornecedorDAO = new FornecedorDAO();
//        this.fornecedorEnderecoDAO = new FornecedorEnderecoDAO();
//        this.produtoFornecedorDAO = new ProdutoFornecedorDAO();
//        this.fornecedorContatoDAO = new FornecedorContatoDAO();
//        this.anterioresDAO = new FornecedorAnteriorDAO();
//        this.municipioDAO = new MunicipioDAO();
//        this.pagamentoDAO = new FornecedorPagamentoDAO();
//        this.fornecedorPrazoDAO = new FornecedorPrazoDAO();
//        this.fornecedorPagamenDAO = new FornecedorPagamentoDAO();
//        this.fornecedorPrazoPedido = new FornecedorPrazoPedidoDAO();
//    }

    public UsuarioRepositoryProvider(String sistema, String lojaOrigem, int lojaVR, int idConexao) throws Exception {
        this.idConexao = idConexao;
        this.sistema = sistema;
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.usuarioDAO = new UsuarioDAO();
//        this.fornecedorEnderecoDAO = new FornecedorEnderecoDAO();
//        this.produtoFornecedorDAO = new ProdutoFornecedorDAO();
//        this.fornecedorContatoDAO = new FornecedorContatoDAO();
        this.anterioresDAO = new UsuarioAnteriorDAO();
//        this.municipioDAO = new MunicipioDAO();
//        this.pagamentoDAO = new FornecedorPagamentoDAO();
//        this.fornecedorPrazoDAO = new FornecedorPrazoDAO();
//        this.fornecedorPagamenDAO = new FornecedorPagamentoDAO();
//        this.fornecedorPrazoPedido = new FornecedorPrazoPedidoDAO();
    }

    public int getIdConexao() {
        return idConexao;
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
  
    public Set<OpcaoUsuario> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(Set<OpcaoUsuario> opcoes) {
        this.opcoes = opcoes;
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



//    public String getSistema() {
//        return sistema;
//    }
//
//    public String getLojaOrigem() {
//        return lojaOrigem;
//    }
//
//    public int getLojaVR() {
//        return lojaVR;
//    }
//
//    public void begin() throws Exception {
//        Conexao.begin();
//    }
//
//    public void rollback() throws Exception {
//        Conexao.rollback();
//    }
//
//    public void commit() throws Exception {
//        Conexao.commit();
//    }

    public void setMaximum(int size) throws Exception {
        ProgressBar.setMaximum(size);
    }

    public void next() throws Exception {
        ProgressBar.next();
    }

    public MultiMap<String, UsuarioAnteriorVO> getAnteriores() throws Exception {
        return anterioresDAO.getAnteriores();
    }

//    public MunicipioVO getMunicipio(int ibge_municipio) throws Exception {
//        return municipioDAO.getMunicipio(ibge_municipio);
//    }
//
//    public MunicipioVO getMunicipio(String municipio, String uf) throws Exception {
//        return municipioDAO.getMunicipio(municipio, uf);
//    }
//
//    public MunicipioVO getMunicipioPadrao() throws Exception {
//        return Parametros.get().getMunicipioPadrao2();
//    }

    public MultiMap<String, UsuarioVO> getLoginExistentes() throws Exception {
        return usuarioDAO.getLoginExistentes();
    }

    public void gravarUsuario(UsuarioVO vo) throws Exception {
        usuarioDAO.gravarUsuario(vo);
    }

    public void gravarUsuarioAnterior(UsuarioAnteriorVO anterior) throws Exception {
        anterioresDAO.gravarUsuarioAnterior(anterior);
    }

    public UsuarioIDStack getIdsExistentes() throws Exception {
        return new UsuarioIDStack();
    }

//    public MultiMap<String, Integer> getContatos() throws Exception {
//        return fornecedorContatoDAO.getContatos();
//    }
//
//    public MultiMap<String, Void> getPagamentos() throws Exception {
//        return fornecedorPagamenDAO.getPagamentos();
//    }
//
//    public MultiMap<String, Void> getDivisoes() throws Exception {
//        return fornecedorPrazoDAO.getDivisoes(getLojaVR());
//    }
//
//    public void gravarFornecedorContato(FornecedorContatoVO contato) throws Exception {
//        fornecedorContatoDAO.salvar(contato);
//    }
//
//    public void gravarCondicaoPagamento(int id, int condicaoPagamento) throws Exception {
//        pagamentoDAO.salvar(id, condicaoPagamento);
//    }
//
//    public void gravarCondicaoPagamento(FornecedorPagamentoVO pagamento) throws Exception {
//        pagamentoDAO.salvar(pagamento);
//    }
//
//    public void gravarPrazoFornecedor(int id, int divisao, int prazoEntrega, int prazoVisita, int prazoSeguranca) throws Exception {
//        fornecedorPrazoDAO.salvar(getLojaVR(), id, divisao, prazoEntrega, prazoVisita, prazoSeguranca);
//    }
//
//    public void gravarPrazoPedidoFornecedor(int idFornecedor, int prazoPedidoEntrega) throws Exception {
//        fornecedorPrazoPedido.salvarTodasLojas(idFornecedor, prazoPedidoEntrega);
//    }
//
//    public void atualizarFornecedor(FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
//        fornecedorDAO.atualizarFornecedor(vo, opt);
//    }
//
//    public void atualizarProdutoFornecedor(ProdutoFornecedorVO vo, Set<OpcaoProdutoFornecedor> opt) throws Exception {
//        produtoFornecedorDAO.atualizarProdutoFornecedor(vo, opt);
//    }
//
//    public void resetCnpjCpf() throws Exception {
//        fornecedorDAO.resetCnpjCpf(getSistema(), getLojaOrigem());
//    }
//
//    public void atualizarContato(FornecedorContatoVO contato, Set<OpcaoFornecedor> opt) throws Exception {
//        fornecedorContatoDAO.atualizar(contato, opt);
//    }
//
//    void gravarFornecedorEndereco() {
//        fornecedorEnderecoDAO.gravarFornecedorEndereco(getSistema(), getLojaOrigem());
//    }
//
//    void atualizarFornecedorEndereco() {
//        fornecedorEnderecoDAO.atualizarFornecedorEndereco();
//        fornecedorEnderecoDAO.atualizarFornecedorEndereco();
//    }

    public TipoSetorVO getTipoSetor(Integer idTipoSetor) throws Exception {
        if (setores == null) {
            setores = new TipoSetorDAO().getAnteriores();
        }
        return setores.get(sistema, lojaOrigem, idTipoSetor.toString());
    }
}
