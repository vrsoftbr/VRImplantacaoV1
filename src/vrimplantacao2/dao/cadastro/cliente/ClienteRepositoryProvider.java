package vrimplantacao2.dao.cadastro.cliente;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialContatoVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.local.MunicipioVO;

/**
 *
 * @author Leandro
 */
public class ClienteRepositoryProvider {
    
    private String sistema;
    private String lojaOrigem;
    private int lojaVR;
    private ClientePreferencialDAO clientePreferencialDAO;
    private Map<Integer, MunicipioVO> municipioByID;
    private MultiMap<String, MunicipioVO> municipioByDesc;

    public ClienteRepositoryProvider() throws Exception {
        this.evt = new OrgEventual(this);
        this.pref = new OrgPreferencial(this);
        this.clientePreferencialDAO = new ClientePreferencialDAO();
    }
    
    public void carregarMunicipios() throws Exception {        
        municipioByID = new LinkedHashMap<>();
        municipioByDesc = new MultiMap<>();
        for (MunicipioVO municipio: new LocalDAO().getMunicipios()) {
            municipioByID.put(municipio.getId(), municipio);
            municipioByDesc.put(municipio, municipio.getEstado().getSigla(), municipio.getDescricao());
        }
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

    

    public static class OrgPreferencial {
        
        ClienteRepositoryProvider provider;
        public OrgPreferencial(ClienteRepositoryProvider prov) throws Exception {
            this.anteriorDAO = new ClientePreferencialAnteriorDAO();
            this.preferencialDAO = new ClientePreferencialDAO();
            this.preferencialContatoDAO = new ClientePreferencialContatoDAO();
            this.anteriorDAO.createTable();
            this.provider = prov;
        }

        public ClienteRepositoryProvider getProvider() {
            return provider;
        }

        private final ClientePreferencialDAO preferencialDAO;
        private final ClientePreferencialAnteriorDAO anteriorDAO;
        private final ClientePreferencialContatoDAO preferencialContatoDAO;
        
        public void salvar(ClientePreferencialVO cliente) throws Exception {
            preferencialDAO.salvar(cliente);
        }

        public void salvar(ClientePreferencialAnteriorVO anterior) throws Exception {
            anteriorDAO.salvar(anterior);
        }        
        
        public void salvar(ClientePreferencialContatoVO contato) throws Exception {
            preferencialContatoDAO.salvar(contato);
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
}
