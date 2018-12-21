package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao2.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao2.dao.cadastro.MercadologicoDAO;
import vrimplantacao2.dao.cadastro.comprador.CompradorAnteriorDAO;
import vrimplantacao2.dao.cadastro.fiscal.pautafiscal.PautaFiscalDAO;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.NcmDAO;
import vrimplantacao2.dao.cadastro.produto.OfertaDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.PisCofinsDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAliquotaDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorEanDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAutomacaoDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoComplementoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.FamiliaProdutoVO;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorEanVO;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoDescontoVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoLojaVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.oferta.OfertaVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.PisCofinsVO;

/**
 * Classe Proxy utilizada para fornecer dados do banco Postgres ao 
 * ProdutoRepository.
 * @author Leandro
 */
public class ProdutoRepositoryProvider {
    
    private String sistema;
    private String loja;
    private int lojaVR;
    private MultiMap<String, FamiliaProdutoVO> familias;
    private MercadologicoDAO mercadologicoDAO = new MercadologicoDAO();  
    private ProdutoDAO produtoDAO = new ProdutoDAO(); 
    private Set<OpcaoProduto> opcoes = new HashSet<>();    
    private PautaFiscalDAO pautaDao = new PautaFiscalDAO();

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
    }

    public Set<OpcaoProduto> getOpcoes() {
        return opcoes;
    }
    
    public Map<Integer, ProdutoVO> getProdutos() throws Exception {
        return produtoDAO.getProdutos();
    }
    
    public void setOpcoes(Set<OpcaoProduto> opcoes) {
        this.opcoes = opcoes;
    }
    
    public void setOpcoes(OpcaoProduto... opcoes) {
        setOpcoes(new HashSet(Arrays.asList(opcoes)));
    }

    public ProdutoIDStack getIDStack() throws Exception {
        return new ProdutoIDStack(new ProdutoIDStackProvider());
    }
    
    public FamiliaProdutoVO getFamiliaProduto(String idFamiliaProduto) throws Exception {
        if (familias == null) {
            familias = new FamiliaProdutoDAO().getAnteriores();
        }
        return familias.get(sistema, loja, idFamiliaProduto);
    }

    public MercadologicoVO getMercadologico(String codMercadologico1, 
            String codMercadologico2, String codMercadologico3, 
            String codMercadologico4, String codMercadologico5) 
            throws Exception{
        return mercadologicoDAO.getMercadologico(
                sistema,
                loja,
                codMercadologico1,
                codMercadologico2,
                codMercadologico3,
                codMercadologico4,
                codMercadologico5
        );
    }

    public void salvar(ProdutoVO prod) throws Exception {
        produtoDAO.salvar(prod);
    }

    public void atualizar(ProdutoVO prod, Set<OpcaoProduto> opt) throws Exception {
        produtoDAO.atualizar(prod, opt);
    }

    public List<LojaVO> getLojas() throws Exception {
        if (lojas == null) {
            lojas = new LojaDAO().carregar();
        }
        return lojas;
    }
    private List<LojaVO> lojas;

    private int nivelMaximoMercadologico = 0;
    public int getNivelMaximoMercadologico() throws Exception {
        if (nivelMaximoMercadologico == 0) {
            nivelMaximoMercadologico = mercadologicoDAO.getNivelMaximoMercadologico();
        }
        return nivelMaximoMercadologico;
    }

    /**
     * Retorna {@link Map} com IDs de fornecedores importados.
     * @return {@link Map} com os IDs importados.
     * @throws Exception 
     */
    public Map<String, Integer> getFornecedoresImportados() throws Exception {
        return new FornecedorAnteriorDAO().getFornecedoresImportados(sistema, loja);
    }

    public Map<String, Integer> getPautaExcecao() throws Exception {
        return pautaDao.getPautaExcecao(sistema, loja);
    }

    /**
     * Retorna um {@link Map} com os IDs de compradores importados.
     * @return {@link Map} com os IDs importados.
     * @throws java.lang.Exception
     */
    public Map<String, Integer> getCompradores() throws Exception {
        return new CompradorAnteriorDAO().getCompradoresImportador(sistema, loja);
    }

    Map<Long, Integer> getEansCadastrados() {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
    public class Anterior {
        
        private ProdutoAnteriorDAO dao = new ProdutoAnteriorDAO();

        public ProdutoAnteriorVO get(String... keys) throws Exception {
            dao.setImportSistema(getSistema());
            dao.setImportLoja(getLoja());
            return dao.getCodigoAnterior().get(keys);
        }

        public void salvar(ProdutoAnteriorVO anterior) throws Exception {
            dao.setImportSistema(getSistema());
            dao.setImportLoja(getLoja());
            dao.salvar(anterior);
        }

        public void alterar(ProdutoAnteriorVO anterior) throws Exception {
            dao.setImportSistema(getSistema());
            dao.setImportLoja(getLoja());
            dao.alterar(anterior);
        }
        
        public boolean cadastrado(String id) throws Exception {
            dao.setImportSistema(getSistema());
            dao.setImportLoja(getLoja());
            return dao.getCodigoAnterior().containsKey(getSistema(), getLoja(), id);
        }
    
    }
    
    public class Complemento {
        
        private ProdutoComplementoDAO produtoComplementoDAO = new ProdutoComplementoDAO();

        public void salvar(ProdutoComplementoVO complemento, boolean unificacao) throws Exception {
            produtoComplementoDAO.salvar(complemento, unificacao);
        }

        public void criarEstoqueAnteriorTemporario() throws Exception {
            produtoComplementoDAO.criarEstoqueAnteriorTemporario(getLojaVR());
        }

        public void atualizar(ProdutoComplementoVO complemento, Set<OpcaoProduto> opt) throws Exception {
            produtoComplementoDAO.atualizar(complemento, opt);
        }

        public void copiarProdutoComplemento(int lojaModelo, int lojaNova) throws Exception {
            produtoComplementoDAO.copiarProdutoComplemento(lojaModelo, lojaNova);
        }

        public void gerarLogDeImportacaoDeEstoque() throws Exception {
            produtoComplementoDAO.gerarLogDeEstoqueViaTMP_ESTOQUE(getLojaVR());
        }
        
    }
    
    public class Aliquota {
        
        private ProdutoAliquotaDAO dao = new ProdutoAliquotaDAO();
 
        public void salvar(ProdutoAliquotaVO aliquota) throws Exception {
            dao.salvar(aliquota);
        }

        public void atualizar(ProdutoAliquotaVO aliquota, Set<OpcaoProduto> opt) throws Exception {
            dao.atualizar(aliquota, opt);
        }

        public MultiMap<Integer, Void> getAliquotas() throws Exception {
            return dao.getAliquotas();
        }

    }
    
    public class Automacao {
        
        private ProdutoAutomacaoDAO dao = new ProdutoAutomacaoDAO();

        public boolean cadastrado(long ean) throws Exception {
            return dao.getEansCadastrados().containsKey(ean);
        }
        
        public int getIdProdutoPorEAN(long ean) throws Exception {
            Integer get = dao.getEansCadastrados().get(ean);
            return get == null ? 0 : get;
        }

        public void salvar(ProdutoAutomacaoVO automacao) throws Exception {
            dao.salvar(automacao);
        }

        public void atualizar(ProdutoAutomacaoVO automacao, Set<OpcaoProduto> opt) throws Exception {
            dao.atualizar(automacao, opt);
        }
    
    }
    
    public class EanAnterior {

        private ProdutoAnteriorEanDAO dao = new ProdutoAnteriorEanDAO();
        
        public boolean cadastrado(String impid, String ean) throws Exception {
            return dao.getEansAnteriores().containsKey(getSistema(), getLoja(), impid, ean);
        }

        public void salvar(ProdutoAnteriorEanVO eanAnterior) throws Exception {
            dao.salvar(eanAnterior);
        }
    
    }
    
    public class Tributo {
        
        private final NcmDAO ncmDAO = new NcmDAO();
        private final CestDAO cestDAO = new CestDAO();
        private MapaTributacaoDAO mapaDao = new MapaTributacaoDAO();
        private final PisCofinsDAO pisCofinsDAO = new PisCofinsDAO();
        
        public PisCofinsVO getPisConfisCredito(int piscofinsCstCredito) throws Exception {
            return pisCofinsDAO.getPisConfisCredito(piscofinsCstCredito);
        }

        public PisCofinsVO getPisConfisDebito(int piscofinsCstDebito) throws Exception {
            return pisCofinsDAO.getPisConfisDebito(piscofinsCstDebito);
        }

        public NaturezaReceitaVO getNaturezaReceita(int piscofinsCstDebito, int piscofinsNaturezaReceita) throws Exception {
            return pisCofinsDAO.getNaturezaReceita(piscofinsCstDebito, piscofinsNaturezaReceita);
        }

        public Icms getIcms(int icmsCst, double icmsAliq, double icmsReducao) throws Exception {
            return Icms.getIcms(icmsCst, icmsAliq, icmsReducao);
        }

        private EstadoVO ufLoja = null;
        public EstadoVO getUf(int idVrLoja) throws Exception {
            if ( ufLoja == null ) {
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            "select \n" +
                            "	e.id,\n" +
                            "	e.sigla,\n" +
                            "	e.descricao\n" +
                            "from \n" +
                            "	loja l \n" +
                            "	join fornecedor f on \n" +
                            "		l.id_fornecedor = f.id \n" +
                            "	join estado e on\n" +
                            "		f.id_estado = e.id\n" +
                            "where l.id = " + idVrLoja
                    )) {
                        if (rst.next()) {
                            EstadoVO uf = new EstadoVO();
                            uf.setId(rst.getInt("id"));
                            uf.setSigla(rst.getString("sigla"));
                            uf.setDescricao(rst.getString("descricao"));
                            ufLoja = uf;                            
                        }
                    }
                }
            }
            
            return ufLoja;
        }
        
        public CestVO getCest(String cestStr) throws Exception {
            String limpo = String.format("%07d", Utils.stringToInt(cestStr));
            return cestDAO.getCestValido(limpo);
        }

        public NcmVO getNcm(String ncmStr) throws Exception {
            return ncmDAO.getNcm(ncmStr);
        }
        
        private Map<String, Icms> icms;
        public Icms getAliquotaByMapaId(String icmsId) throws Exception {
            if (icms == null) {
                icms = new HashMap<>();
                for (MapaTributoVO vo: mapaDao.getMapa(getSistema(), getLoja())) {
                    if (vo.getAliquota() != null) {
                        icms.put(vo.getOrigId(), vo.getAliquota());
                    }
                }
            }
            
            Icms icm = icms.get(icmsId);
            if (icm == null) {
                icm = Icms.getIsento();
            }
            
            return icm;
        }
        
    }
    
    public static class Atacado {
        
        private final ProdutoAutomacaoLojaDAO produtoAutomacaoLojaDAO = new ProdutoAutomacaoLojaDAO();
        private final ProdutoAutomacaoDescontoDAO produtoAutomacaoDescontoDAO = new ProdutoAutomacaoDescontoDAO();

        public void atualizarLoja(ProdutoAutomacaoLojaVO precoAtacadoLoja, Set<OpcaoProduto> opt) throws Exception {
            produtoAutomacaoLojaDAO.salvar(precoAtacadoLoja, opt);            
        }

        public void atualizarDesconto(ProdutoAutomacaoDescontoVO precoAtacadoDesconto, Set<OpcaoProduto> opt) throws Exception {
            produtoAutomacaoDescontoDAO.salvar(precoAtacadoDesconto, opt);
        }
    
    }
    
    public static class Oferta {
        private ProdutoRepositoryProvider provider;
        private OfertaDAO ofertaDAO = new OfertaDAO();

        public Oferta(ProdutoRepositoryProvider provider) {
            this.provider = provider;
        }

        public MultiMap<Comparable, Void> getCadastradas() throws Exception {
            return ofertaDAO.getCadastradas(provider.getLojaVR());
        }

        public void gravar(OfertaVO vo) throws Exception {
            ofertaDAO.gravar(vo);
        }
        
    }
    
    private Anterior anterior = new Anterior();
    private Complemento complemento = new Complemento();
    private Aliquota aliquota = new Aliquota();
    private Automacao automacao = new Automacao();
    private EanAnterior eanAnterior = new EanAnterior();
    private Tributo tributo = new Tributo();
    private Atacado atacado = new Atacado();
    private Oferta oferta = new Oferta(this);

    
    public Tributo tributo() {
        return tributo;
    }
    public Anterior anterior() {
        return anterior;
    }
    public Complemento complemento() {
        return complemento;
    }
    public Aliquota aliquota() {
        return aliquota;
    }
    public Automacao automacao() {
        return automacao;
    }
    public EanAnterior eanAnterior() {
        return eanAnterior;
    }
    public Atacado atacado() {
        return atacado;
    }
    public Oferta oferta() {
        return oferta;
    }
    
}
