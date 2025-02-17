package vrimplantacao2.dao.cadastro.cliente;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.dao.cadastro.cliente.food.ClienteFoodAnteriorDAO;
import vrimplantacao2.dao.cadastro.cliente.food.ClienteFoodDAO;
import vrimplantacao2.dao.cadastro.cliente.food.ClienteFoodTelefoneDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialDependenteVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.food.ClienteFoodVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;

/**
 *
 * @author Leandro
 */
public class ClienteRepositoryProvider {

    private int idConexao;
    private String sistema;
    private String lojaOrigem;
    private int lojaVR;
    private ClientePreferencialDAO clientePreferencialDAO;
    private ProdutoDAO produtoDAO;
    private Map<Integer, MunicipioVO> municipioByID;
    private MultiMap<String, MunicipioVO> municipioByDesc;
    private boolean desativarNotificacao = false;

    public void setDesativarNotificacao(boolean desativarNotificacao) {
        this.desativarNotificacao = desativarNotificacao;
    }

    public ClienteRepositoryProvider() throws Exception {
        this.evt = new OrgEventual(this);
        this.pref = new OrgPreferencial(this);
        this.food = new OrgClienteFood();
        this.clientePreferencialDAO = new ClientePreferencialDAO();
    }

    public void setNotificacao(String mensagem, int qtd) throws Exception {
        if (!desativarNotificacao) {
            ProgressBar.setStatus(mensagem);
            ProgressBar.setMaximum(qtd);
        }
    }

    public void notificar() throws Exception {
        if (!desativarNotificacao) {
            ProgressBar.next();
        }
    }

    public void carregarMunicipios() throws Exception {
        municipioByID = new LinkedHashMap<>();
        municipioByDesc = new MultiMap<>();
        for (MunicipioVO municipio : new LocalDAO().getMunicipios()) {
            municipioByID.put(municipio.getId(), municipio);
            municipioByDesc.put(municipio, municipio.getEstado().getSigla(), municipio.getDescricao());
        }
    }

    public int getIdConexao() {
        return this.idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLojaOrigem(String lojaOrigem) {
        this.lojaOrigem = lojaOrigem;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
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

    //<editor-fold defaultstate="collapsed" desc="Operação de banco">
    public void begin() throws Exception {
        Conexao.begin();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }
    //</editor-fold>

    public ClientePreferencialIDStack getClientePreferencialIDStack(int iniciarEm) {
        return new ClientePreferencialIDStack(iniciarEm);
    }

    public ClienteEventualIDStack getClienteEventualIDStack(int iniciarEm) {
        return new ClienteEventualIDStack(iniciarEm);
    }

    public MunicipioVO getMunicipioById(int municipioIBGE) throws Exception {
        if (municipioByID == null) {
            carregarMunicipios();
        }
        return municipioByID.get(municipioIBGE);
    }

    public MunicipioVO getMunicipioByNomeUf(String municipio, String uf) throws Exception {
        if (municipioByDesc == null) {
            carregarMunicipios();
        }
        return municipioByDesc.get(uf, municipio);
    }

    public MunicipioVO getMunicipioPadrao() throws Exception {
        return Parametros.get().getMunicipioPadrao2();
    }

    private OrgPreferencial pref;

    public OrgPreferencial preferencial() {
        return pref;
    }

    private OrgEventual evt;

    public OrgEventual eventual() {
        return evt;
    }

    int getProduto() throws Exception {
        return produtoDAO.getProduto();
    }

    long getEan() throws Exception {
        return produtoDAO.getEan(getProduto());
    }

    public static class OrgPreferencial {

        ClienteRepositoryProvider provider;

        public OrgPreferencial(ClienteRepositoryProvider prov) throws Exception {
            this.anteriorDAO = new ClientePreferencialAnteriorDAO();
            this.preferencialDAO = new ClientePreferencialDAO();
            this.preferencialContatoDAO = new ClientePreferencialContatoDAO();
            this.preferencialDependenteDAO = new ClientePreferencialDependenteDAO();
            this.anteriorDAO.createTable();
            this.provider = prov;
        }

        public ClienteRepositoryProvider getProvider() {
            return provider;
        }

        private final ClientePreferencialDAO preferencialDAO;
        private final ClientePreferencialAnteriorDAO anteriorDAO;
        private final ClientePreferencialContatoDAO preferencialContatoDAO;
        private final ClientePreferencialDependenteDAO preferencialDependenteDAO;

        public void salvar(ClientePreferencialVO cliente) throws Exception {
            preferencialDAO.salvar(cliente);
        }

        public void salvar(ClientePreferencialAnteriorVO anterior) throws Exception {
            anteriorDAO.salvar(anterior);
        }

        public void salvar(ClientePreferencialContatoVO contato) throws Exception {
            preferencialContatoDAO.salvar(contato);
        }

        public void salvar(ClientePreferencialDependenteVO dependente) throws Exception {
            preferencialDependenteDAO.salvar(dependente);
        }

        public void atualizar(ClientePreferencialDependenteVO dependente) throws Exception {
            preferencialDependenteDAO.atualizar(dependente);
        }

        public Map<Long, Integer> getCnpjCadastrados() throws Exception {
            return preferencialDAO.getCnpjCadastrados();
        }

        public MultiMap<String, ClientePreferencialAnteriorVO> getAnteriores() throws Exception {
            return anteriorDAO.getAnteriores(provider.getSistema(), provider.getLojaOrigem());
        }

        public MultiMap<String, Void> getContatosExistentes() throws Exception {
            return preferencialContatoDAO.getContatosExistentes();
        }

        public MultiMap<String, Void> getDependentesExistentes() throws Exception {
            return preferencialDependenteDAO.getDependentesExistentes();
        }
    }

    public static class OrgEventual {

        ClienteRepositoryProvider prov;

        public OrgEventual(ClienteRepositoryProvider prov) throws Exception {
            this.anteriorDAO = new ClienteEventualAnteriorDAO();
            this.eventualDAO = new ClienteEventualDAO();
            this.eventualContatoDAO = new ClienteEventualContatoDAO();
            this.anteriorDAO.createTable();
            this.prov = prov;
        }

        private final ClienteEventualDAO eventualDAO;
        private final ClienteEventualAnteriorDAO anteriorDAO;
        private final ClienteEventualContatoDAO eventualContatoDAO;

        public void salvar(ClienteEventualVO cliente) throws Exception {
            eventualDAO.salvar(cliente);
        }

        public void salvar(ClienteEventualAnteriorVO anterior) throws Exception {
            anteriorDAO.salvar(anterior);
        }

        public void salvar(ClienteEventualContatoVO contato) throws Exception {
            eventualContatoDAO.salvar(contato);
        }

        public Map<Long, Integer> getCnpjCadastrados() throws Exception {
            return eventualDAO.getCnpjCadastrados();
        }

        public MultiMap<String, ClienteEventualAnteriorVO> getAnteriores() throws Exception {
            return anteriorDAO.getAnteriores(prov.getSistema(), prov.getLojaOrigem());
        }

        public MultiMap<String, Void> getContatosExistentes() throws Exception {
            return eventualContatoDAO.getContatosExistentes();
        }

        public ClienteRepositoryProvider getProvider() {
            return prov;
        }
    }

    public void atualizarClientePreferencial(ClientePreferencialVO vo, Set<OpcaoCliente> opt) throws Exception {
        clientePreferencialDAO.atualizarClientePreferencial(vo, opt);
    }

    private OrgClienteFood food;

    public OrgClienteFood food() {
        return food;
    }

    public class OrgClienteFood {

        private ClienteFoodAnteriorDAO anteriorDao;
        private ClienteFoodTelefoneDAO clienteFoodTelefoneDao;
        private ClienteFoodDAO clienteFoodDao;

        public OrgClienteFood() throws Exception {
            this.anteriorDao = new ClienteFoodAnteriorDAO();
            this.clienteFoodTelefoneDao = new ClienteFoodTelefoneDAO();
            this.clienteFoodDao = new ClienteFoodDAO();
        }

        public Map<String, ClienteFoodAnteriorVO> getAnteriores() throws Exception {
            return anteriorDao.getAnteriores(getSistema(), getLojaOrigem());
        }

        public Map<Long, ClienteFoodVO> getTelefones() throws Exception {
            return clienteFoodTelefoneDao.getTelefones();
        }

        public IDStack getClienteVrFoodIds() throws Exception {
            return clienteFoodDao.getIds();
        }

        public void gravarClienteFoodAnterior(ClienteFoodAnteriorVO anterior) throws Exception {
            anteriorDao.gravar(anterior);
        }

        public void atualizarClienteFood(ClienteFoodVO codigoAtual, HashSet<OpcaoCliente> opt) throws Exception {
            clienteFoodDao.atualizar(codigoAtual, opt);
        }

        public void gravarClienteFood(ClienteFoodVO codigoAtual) throws Exception {
            clienteFoodDao.gravar(codigoAtual);
        }

        public void atualizarClienteFoodAnterior(ClienteFoodAnteriorVO anterior) throws Exception {
            anteriorDao.atualizar(anterior);
        }

        public void incluirTelefoneFood(int id, Long telefone) throws Exception {
            clienteFoodTelefoneDao.incluirTelefone(id, telefone);
        }
    }
}
