package vrimplantacao2.dao.cadastro.venda;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.EcfDAO;
import vrimplantacao2.dao.cadastro.cliente.ClienteEventualAnteriorDAO;
import vrimplantacao2.dao.cadastro.cliente.ClienteEventualDAO;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialAnteriorDAO;
import vrimplantacao2.dao.cadastro.cliente.ClientePreferencialDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoVO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.cliente.ClienteEventualVO;
import vrimplantacao2.vo.cadastro.cliente.ClientePreferencialVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaItemVO;
import vrimplantacao2.vo.cadastro.venda.PdvVendaVO;
import vrimplantacao2.vo.cadastro.venda.PublicVendaValoresAgrupado;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * Provider para o repositório de vendas.
 * @author Leandro
 */
public class VendaRepositoryProvider {
    
    private final VendaItemFacade item = new VendaItemFacade();
    private final VendaProviderFacade venda = new VendaProviderFacade();
    
    private JdbcConnectionSource source;
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private PdvVendaDAO vendaDAO;
    private PdvVendaItemDAO vendaItemDAO;
    private PublicVendaDAO publicVendaDAO;
    private EcfDAO ecfDAO;
    private ProdutoAnteriorDAO produtoAnteriorDAO;
    private MapaVendaDAO mapaVendaDAO;
    private ClientePreferencialDAO clientePreferencialDAO;
    private ClientePreferencialAnteriorDAO clientePreferencialAnteriorDAO;
    private ClienteEventualDAO clienteEventualDAO;
    private ClienteEventualAnteriorDAO clienteEventualAnteriorDAO;
    private VendaImpDao vendaImpDao;
    private VendaItemImpDao vendaItemImpDao;

    public VendaRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.vendaDAO = new PdvVendaDAO();
        this.vendaItemDAO = new PdvVendaItemDAO(sistema, loja);
        this.ecfDAO = new EcfDAO();
        this.produtoAnteriorDAO = new ProdutoAnteriorDAO();
        this.mapaVendaDAO = new MapaVendaDAO(sistema, loja);
        this.clientePreferencialDAO = new ClientePreferencialDAO();
        this.clientePreferencialAnteriorDAO = new ClientePreferencialAnteriorDAO();
        this.clienteEventualDAO = new ClienteEventualDAO();
        this.clienteEventualAnteriorDAO = new ClienteEventualAnteriorDAO();
        this.source = new JdbcConnectionSource("jdbc:sqlite:" + Parametros.get().getBancoImplantacao());
        this.vendaImpDao = new VendaImpDao(this.source);
        this.vendaItemImpDao = new VendaItemImpDao(this.source);
        this.publicVendaDAO = new PublicVendaDAO();
    }

    public VendaItemFacade item() {
        return item;
    }
    
    public VendaProviderFacade venda() {
        return venda;
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

    public void notificar(String msg) throws Exception {
        ProgressBar.setStatus(msg);
    }
    
    public void notificar(String msg, int size) throws Exception {
        notificar(msg);
        ProgressBar.setMaximum(size);
    }
    
    public void notificar() throws Exception {
        ProgressBar.next();
    }

    public void gravar(PdvVendaVO vo) throws Exception {
        vendaDAO.gravar(vo);
    }

    public void gravar(PdvVendaItemVO item) throws Exception {
        vendaItemDAO.gravar(item);
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
    
    public void eliminarVenda(int ecf, int numeroCupom, Date data, double subTotalImpressora) throws Exception {
        vendaDAO.eliminarVenda(getLojaVR(), ecf, numeroCupom, data, subTotalImpressora);
    }

    public int getMatricula() throws Exception {
        return vendaDAO.getMatricula(getLojaVR());
    }

    public EcfVO getEcf(String numeroSerie) throws Exception {
        return ecfDAO.getEcf(numeroSerie, getLojaVR());
    }

    public Map<String, ProdutoAnteriorVO> getProdutosAnteriores() throws Exception {
        return produtoAnteriorDAO.getCodigoAnterior(getSistema(), getLoja());
    }

    public Icms getAliquota(int cst, double aliquota, double reduzido) throws Exception {
        return Icms.getIcms(cst, aliquota, reduzido);
    }

    public Icms getIsento() throws Exception {
        return Icms.getIsento();
    }

    public Integer getProdutoPorCodigoAnterior(String produto) throws Exception {
        return vendaItemDAO.getProdutoPorCodigoAnterior(produto);
    }

    public Integer getProdutoPorCodigoAnteriorSemUltimoDigito(String produto) throws Exception {
        return vendaItemDAO.getProdutoPorCodigoAnteriorSemUltimoDigito(produto);
    }
    
    public Integer getProdutoPorEANAnterior(String ean) throws Exception {
        return vendaItemDAO.getProdutoPorEANAnterior(ean);
    }

    public Integer getProdutoPorEANAtual(long ean) throws Exception {
        return vendaItemDAO.getProdutoPorEANAtual(ean);
    }

    public Integer getProdutoPorMapeamento(String codigoItem) throws Exception {
        return mapaVendaDAO.getProdutoPorMapeamento(codigoItem);
    }

    public void gravarMapa(String produto, String codigoBarras, String descricaoReduzida) throws Exception {
        mapaVendaDAO.gravar(produto, codigoBarras, descricaoReduzida);
    }

    public void logarVendaImportadas(long id_venda) throws Exception {        
        vendaDAO.logarVendaImportadas(id_venda);
    }

    public Map<String, ClientePreferencialVO> getClientesPreferenciaisAnteriores() throws Exception {
        return clientePreferencialAnteriorDAO.getClientesAnteriores(getSistema(), getLoja());
    }

    Map<Long, ClientePreferencialVO> getClientesPorCnpj() throws Exception {
        return clientePreferencialDAO.getClientesPorCnpj();
    }

    public Map<String, ClienteEventualVO> getClientesEventuaisAnteriores() throws Exception {
        return clienteEventualAnteriorDAO.getAnterior(getSistema(), getLoja());
    }

    public Map<Long, ClienteEventualVO> getClientesEventuaisPorCnpj() throws Exception {
        return clienteEventualDAO.getClientesPorCnpj();
    }

    public Iterator<VendaIMP> getVendaIMP(long limit, long offSet) throws Exception {
        return vendaImpDao.getVendas(limit, offSet);
    }

    public List<VendaItemIMP> getVendaItemIMP(String vendaId) throws Exception {
        return vendaItemImpDao.getVendaItens(vendaId);
    }

    public long getVendaImpSize() throws Exception {
        return vendaImpDao.getCount();
    }

    public void gerarMapaResumo() throws Exception {
        vendaDAO.gerarMapaResumo(getLojaVR());
    }

    public void gerarECFs() throws Exception {
        vendaDAO.gerarECFs(getLojaVR());
    }

    public void gerarRegistrosGenericos() throws Exception {
        vendaDAO.gerarRegistrosGenericos();
    }

    public void vincularMapaDivergenciaComAnteriores() throws Exception {
        vendaDAO.vincularMapaDivergenciaComAnteriores(getSistema(), getLoja());
    }

    public void gerarConsistencia() throws Exception {
        vendaDAO.gerarConsistencia(getLojaVR());
    }

    private Map<String, Icms> icms;
    private MapaTributacaoDAO mapaDao = new MapaTributacaoDAO();
    public Icms getAliquota(String icmsAliquotaId) throws Exception {
        if (icms == null) {
            icms = new HashMap<>();
            for (MapaTributoVO vo: mapaDao.getMapa(getSistema(), getLoja())) {
                if (vo.getAliquota() != null) {
                    icms.put(vo.getOrigId(), vo.getAliquota());
                }
            }
        }
        
        Icms icm = icms.get(icmsAliquotaId);
        if (icm == null) {
            icm = Icms.getIsento();
        }

        return icm;
    }

    public List<VendaItemIMP> getProdutosVendidos() throws SQLException {
        return vendaItemImpDao.getProdutosVendidos();
    }

    public Long gravarPublicVenda(PublicVendaValoresAgrupado item) throws Exception {
        return publicVendaDAO.gravarPublicVenda(item);
    }

    List<PublicVendaValoresAgrupado> carregarVendasExistentesNaBase(String dataAtual, int lojaVR) throws Exception {
        return publicVendaDAO.carregaVendasExistentes(dataAtual, lojaVR);
    }

    public Long atualizarPublicVenda(PublicVendaValoresAgrupado vendasBancoVR) throws Exception {
        return publicVendaDAO.atualizarpublicVenda(vendasBancoVR);
    }

    public List<PublicVendaValoresAgrupado> carregarVendasImportadas(String menorData, String maiorData, int lojaVR) throws Exception {
        return publicVendaDAO.carregarVendasImportadas(menorData, maiorData, lojaVR);
    }

    void logarPublicVendaImportadas(Long id) throws Exception {
        publicVendaDAO.logarPublicVendaImportadas(id);
    }
    
    public final class VendaItemFacade {
        private VendaItemFacade() {}

        public void atualizar(int idVenda, PdvVendaItemVO item) throws Exception {
            vendaItemDAO.atualizar(idVenda, item);
        }
    }
    
    public final class VendaProviderFacade {
        private VendaProviderFacade() {}

        public Integer encontrarVenda(int ecf, int numeroCupom, Date data, double subTotalImpressora) throws Exception {
            return vendaDAO.encontrarVenda(getLojaVR(), ecf, numeroCupom, data, subTotalImpressora);
        }
    }

}
