package vrimplantacao2.parametro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import vrimplantacao.DadosConexaoPostgreSQL;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.MunicipioVO;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.dao.cadastro.financeiro.diversos.TipoPagamentoDAO;
import vrimplantacao2.dao.cadastro.local.MunicipioDAO;
import vrimplantacao2.utils.logging.LoggingConfig;
import vrimplantacao2.utils.logging.LoggingType;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.TipoPagamento;

/**
 * Classe responsável por manipular parâmetros armazenados no banco de dados.
 * @author Leandro Caires
 */
public class Parametros implements Iterable<Parametro>{
    
    private static final Logger LOG = Logger.getLogger(Parametros.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="Variáveis e constantes">    
    private static final String PARAMETROS_CONFIGURADOS = "PARAMETROS_CONFIGURADOS";
    private static final String UF = "UF";
    private static final String CEP = "CEP";
    private static final String LOCAL = "LOCAL";
    private static final String MUNICIPIO = "MUNICIPIO";
    private static final String BANCO_IMPL = "BANCO_IMPL";
    private static final String GERAR_BANCO_IMPLANTACAO = "GERAR_BANCO_IMPLANTACAO";
    private static final String IMPORTAR_BANCO_IMPLANTACAO = "IMPORTAR_BANCO_IMPLANTACAO";
    private static final String ITEM_VENDA_PADRAO = "ITEM_VENDA_PADRAO";
    private static final String IGNORAR_CLIENTE_IMP_VENDA = "IGNORAR_CLIENTE_IMP_VENDA";
    private static final String FORCAR_CADASTRO_PRODUTO_NAO_EXISTENTE = "FORCAR CADASTRO DE PRODUTO NAO EXISTENTE";
    private static final String IMPORTAR_ICMS_ISENTO_MIGRACAO_PRODUTO = "IMPORTAR_ICMS_ISENTO_MIGRACAO_PRODUTO";
    private static final SimpleDateFormat DATA_FORMAT= new SimpleDateFormat("yyyy-MM-dd");
    public static String lite;
    
    private final ParametroImportacaoDAO dao = new ParametroImportacaoDAO();
    private final MultiMap<String, String> params = new MultiMap<>();
    
    private EstadoVO ufCache;
    private MunicipioVO municipioCache;
    private vrimplantacao2.vo.cadastro.local.MunicipioVO municipioCache2;
    private TipoPagamento tipoPagamento;
    private boolean ignorarClienteImpVenda;
    
    private Map<String, LoggingConfig> loggers;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="SINGLETON">
    private static Parametros singleton;
    private static Factory factory = new Factory();
    public static void setFactory(Factory factory) {
        Parametros.factory = factory;
    }
    
    public static class Factory {
        public Parametros newInstance() {
            try {
                return new Parametros();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
    
    public static Parametros get() {
        if (singleton == null) {
            singleton = factory.newInstance();
        }
        return singleton;
    }
    private Parametros() throws Exception {
        carregar();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Métodos para facilitar a manipulação dos parâmetros">
    @Override
    public Iterator<Parametro> iterator() {
        List<Parametro> result = new ArrayList<>();
        for (KeyList<String> keys: params.keySet()) {
            String[] chave = keys.toArray();
            String value = params.get(chave);
            result.add(new Parametro(Arrays.asList(chave), value));
        }
        return result.iterator();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Operações com o banco de dados">
    /**
     * Atualiza a listagem de parâmetros com os dados do banco.
     * @throws Exception
     */
    public void carregar() throws Exception {
        params.clear();
        for (Parametro param: dao.getParametros()) {
            params.put(param.getValue(), param.getKeys().toArray(new String[] {}));
        }
        ufCache = new LocalDAO().getEstado(Utils.stringToInt(params.get(LOCAL, UF)), false);
        municipioCache = new LocalDAO().getMunicipio(Utils.stringToInt(params.get(LOCAL, MUNICIPIO)));
        municipioCache2 = new MunicipioDAO().getMunicipio(Utils.stringToInt(params.get(LOCAL, MUNICIPIO)));
        tipoPagamento = params.get("TIPO_PAGAMENTO_PADRAO") != null ? new TipoPagamentoDAO().getById(Utils.stringToInt(params.get("TIPO_PAGAMENTO_PADRAO"))) : null;
        
        loggers = new LinkedHashMap<>();
        
        String get = params.get("LOG","NAMES");
        if (get == null) {
            get = "[]";
        }
        
        JSONArray logs = new JSONArray(get);
        for (int i = 0; i < logs.length(); i++) {
            JSONObject json = logs.getJSONObject(i);
            
            LoggingConfig log = new LoggingConfig(json.getString("name"));
            log.setLevel(Level.parse(json.getString("level")));
            log.setType(LoggingType.valueOf(json.getString("type")));
            
            loggers.put(log.getNome(), log);            
        }
        
        LogManager.getLogManager().reset();
        
        for (LoggingConfig log: loggers.values()) {
            Logger logger = Logger.getLogger(log.getNome());            
            logger.setLevel(log.getLevel());
            logger.addHandler(log.getType().getHandler());
        }
       
        if (getBancoImplantacao() == null) {
            setBancoImplantacao("impl_" + new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()) + ".db");
        }
        
        LOG.config("Parametros carregados com sucesso");
    }
    
    /**
     * Grava os parâmetros no banco.
     * @throws Exception
     */
    public void salvar() throws Exception {
        
        LOG.fine("Convertendo os loggers em JSON para gravação");
        
        JSONArray array = new JSONArray();
        
        for (LoggingConfig config: loggers.values()) {
            
            JSONObject json = new JSONObject();
            json.put("name", config.getNome());
            json.put("type", config.getType().toString());
            json.put("level", config.getLevel().getName());
            
            array.put(json);
            
            LOG.finest("JSON convertido " + json.toString());
        }
        
        params.put(array.toString(), "LOG","NAMES");       
        
        LOG.finest("Formato final do JSON\n" + array.toString(2));
        
        dao.salvar(this);
        
        LOG.config("Configurações dos parâmetros foram gravadas com sucesso!");
        
        carregar();
    }
    //</editor-fold>
    
    public void setGerarBancoImplantacao(boolean gerarBancoImplantacao) {
        try {
            put(gerarBancoImplantacao, GERAR_BANCO_IMPLANTACAO);
            LOG.finer("Gerar dados no banco implantacao alterado");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);            
        }
    }
    
    public void setImportarBancoImplantacao(boolean importarBancoImplantacao) {
        try {
            put(importarBancoImplantacao, IMPORTAR_BANCO_IMPLANTACAO);
            LOG.finer("Importar dados do banco implantacao alterado");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);            
        }
    }
    
    public void setBancoImplantacao(String bancoImplantacao) {
        try {
            put(bancoImplantacao, BANCO_IMPL);
            LOG.finer("Banco SQLite da implantação alterado");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);            
        }
    }

    public void setUfPadrao(int id) {
        try {
            ufCache = new LocalDAO().getEstado(id, false);
            params.put(String.valueOf(id), LOCAL, UF);
            LOG.finer("Uf padrão alterado");
        } catch (Exception ex) {
            ufCache = null;
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);
        }
    }

    public void setMunicipioPadrao(int id) {
        try {
            municipioCache = new LocalDAO().getMunicipio(id);
            params.put(String.valueOf(id), LOCAL, MUNICIPIO);
            LOG.finer("Municipio padrão alterado");
        } catch (Exception ex) {
            municipioCache = null;
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);
        }
    }
    
    public void setMunicipioPadrao2(int id) {
        try {
            municipioCache2 = new MunicipioDAO().getMunicipio(id);
            params.put(String.valueOf(id), LOCAL, MUNICIPIO);
            LOG.finer("Municipio padrão alterado");
        } catch (Exception ex) {
            municipioCache = null;
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);
        }
    }

    public void setCepPadrao(int cep) {
        params.put(String.valueOf(cep), LOCAL, CEP);
        LOG.finer("CEP padrão alterado");
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
        if (tipoPagamento != null) {
            params.put(String.valueOf(tipoPagamento.getId()), "TIPO_PAGAMENTO_PADRAO");
        }
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }
    

    public void setParametrosConfigurados(boolean configurado) {
        params.put(String.valueOf(configurado), PARAMETROS_CONFIGURADOS);
    }

    public EstadoVO getUfPadrao() {
        return ufCache;
    }
    
    public boolean isGerarBancoImplantacao() {
        return getBool(GERAR_BANCO_IMPLANTACAO);
    }
    
    public boolean isImportarBancoImplantacao() {
        return getBool(IMPORTAR_BANCO_IMPLANTACAO);
    }
    
    public vrimplantacao2.vo.cadastro.local.EstadoVO getUfPadraoV2() {        
        return new vrimplantacao2.vo.cadastro.local.EstadoVO(ufCache.getId(), ufCache.getSigla(), ufCache.getDescricao());
    }

    public Map<String, LoggingConfig> getLoggers() {
        return loggers;
    }

    @Deprecated
    public MunicipioVO getMunicipioPadrao() {
        return municipioCache;
    }
    
    public vrimplantacao2.vo.cadastro.local.MunicipioVO getMunicipioPadrao2() {
        return municipioCache2;
    }

    public int getCepPadrao() {
        return this.getInt(LOCAL, CEP);
    }
    
    public String getBancoImplantacao () {
        return params.get(BANCO_IMPL);
    }
    
    public boolean isParametroConfigurado() {
        return getBool(PARAMETROS_CONFIGURADOS);
    }

    public int getItemVendaPadrao() {
        return Utils.stringToInt(params.get(ITEM_VENDA_PADRAO));
    }

    public void setItemVendaPadrao(int itemVendaPadrao) {
        this.params.put(itemVendaPadrao == 0 ? null : String.valueOf(itemVendaPadrao), ITEM_VENDA_PADRAO);
    }

    /**
     * Inclui um parâmetro como String.
     * @param param Valor do parâmetro.
     * @param keys Identificação do parâmetro.
     */
    public void put(String param, String... keys) {
        params.put(param, keys);
    }
    
    /**
     * Inclui um int na listagem dos parâmetros.
     * @param param Valor do parâmetro.
     * @param keys Identificação do parâmetro.
     */
    public void put(int param, String... keys) {
        params.put(String.valueOf(param), keys);
    }
    
    /**
     * Inclui um double na listagem de parâmetros.
     * @param param Valor do parâmetro.
     * @param keys Identificação do parâmetro.
     */
    public void put(double param, String... keys) {
        params.put(String.valueOf(param), keys);
    }
    
    /**
     * Inclui um parâmetro como Boolean.
     * @param param Valor do parâmetro.
     * @param keys Identificação do parâmetro.
     */
    public void put(boolean param, String... keys) {
        params.put(String.valueOf(param), keys);
    }
    
    /**
     * Inclui uma data na listagem de parâmetros.
     * @param param Valor do parâmetro.
     * @param keys Identificação do parâmetro.
     */
    public void put(Date param, String... keys) {
        if (param != null) {
            String valor = DATA_FORMAT.format(param);
            params.put(valor, keys);
        } else {
            params.put(null, keys);
        }
    }

    /**
     * Retorno o valor do parâmetro.
     * @param keys Chaves para localizar o valor.
     * @return Valor do parâmetro como String.
     */
    public String get(String... keys) {
        return params.get(keys);
    }
    
    public String getWithNull(String nullValue, String... keys) {
        String value = params.get(keys);
        if (value != null && !"".equals(value.trim())) {
            return value;
        } else {
            return nullValue;
        }
    }
    
    /**
     * Retorna o valor de um parametro como inteiro.
     * @param keys Chaves para localizar o valor.
     * @return Valor do parâmetro como int ou 0 caso null ou vazio.
     */
    public int getInt(String... keys) {
        return getInt(0, keys);
    }
    
    public int getInt(int nullValue, String... keys) {
        String get = params.get(keys);
        if (get != null && !"".equals(get.trim())) {
            return Integer.parseInt(get);
        } else {
            return nullValue;
        }
    }
    
    /**
     * Retorna um valor de parâmetro como boolean;
     * @param keys
     * @return
     */
    public boolean getBool(String... keys) {
        return getBool(false, keys);
    }
    
    public boolean getBool(boolean nullValue, String... keys) {
        String bool = params.get(keys);
        if (bool == null || "".equals(bool)) {
            return nullValue;
        } else {
            return bool.toLowerCase().matches("true|verdadeiro|1|S|s");
        }
    }
    
    /**
     * Retorna o valor de um parametro como double.
     * @param keys Chaves para localizar o valor.
     * @return Valor do parâmetro como double ou 0 caso null ou vazio.
     */
    public double getDouble(String... keys) {
        return getDouble(0, keys);
    }
    
    public double getDouble(double nullValue, String... keys) {
        String get = params.get(keys);
        if (get != null && !"".equals(get.trim())) {
            return Double.parseDouble(get);
        } else {
            return nullValue;
        }
    }
    
    /**
     * Retorna o valor de um parametro como {@link Date}.
     * @param keys Chaves para localizar o valor.
     * @return Valor do parâmetro como {@link Date} ou null.
     */
    public Date getDate(String... keys) {
        return getDate(null, keys);
    }
    
    public Date getDate(Date nullValue, String... keys) {
        String get = params.get(keys);
        if (get != null && !"".equals(get.trim())) {            
            try {
                return new Date(DATA_FORMAT.parse(get).getTime());
            } catch (ParseException ex) {
                throw new RuntimeException("Erro ao converter a Data", ex);
            }
        } else {
            return nullValue;
        }
    }

    /**
     * @return the verificaClienteImpVenda
     */
    public boolean isIgnorarClienteImpVenda() {
        return getBool(IGNORAR_CLIENTE_IMP_VENDA);
    }
    
    /**
     * @return the verificaProdutoNaoExistente
     */
    public boolean isForcarCadastroProdutoNaoExistente() {
        return getBool(FORCAR_CADASTRO_PRODUTO_NAO_EXISTENTE);
    }
    
    /**
     * @return the importar icms Isento
     */
    public boolean isImportarIcmsIsentoMigracaoProduto() {
        return getBool(IMPORTAR_ICMS_ISENTO_MIGRACAO_PRODUTO);
    }

    /**
     * @param ignorarClienteImpVenda the verificaClienteImpVenda to set
     */
    public void setIgnorarClienteImpVenda(boolean ignorarClienteImpVenda) {
        try {
            put(ignorarClienteImpVenda, IGNORAR_CLIENTE_IMP_VENDA);
            LOG.finer("Importar dados do banco implantacao alterado");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);            
        }
    }

    /**
     * @param forcarCadastroProdutoNaoExistenteImpVenda the verificaProdutoImpVenda to set
     */
    public void setForcarCadastroProdutoNaoExistenteImpVenda(boolean forcarCadastroProdutoNaoExistenteImpVenda) {
        try {
            put(forcarCadastroProdutoNaoExistenteImpVenda, FORCAR_CADASTRO_PRODUTO_NAO_EXISTENTE);
            LOG.finer("Importar dados do banco implantacao alterado");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);            
        }
    }
    
    /**
     * @param importarIcmsIsentoMigracaoProduto 
     */
    public void setImportarIcmsIsentoMigracaoProduto(boolean importarIcmsIsentoMigracaoProduto) {
        try {
            put(importarIcmsIsentoMigracaoProduto, IMPORTAR_ICMS_ISENTO_MIGRACAO_PRODUTO);
            LOG.finer("Importar dados do banco implantacao alterado");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro ao gravar", ex);
            throw new RuntimeException(ex);
        }
    }
    
    private DadosConexaoPostgreSQL empresaAtiva;
    public void setEmpresaAtiva(DadosConexaoPostgreSQL empresaAtiva) {
        this.empresaAtiva = empresaAtiva;
    }
    public DadosConexaoPostgreSQL getEmpresaAtiva() {
        return empresaAtiva;
    }

    public void setNaoImportarPautaSeNcmNaoExistir(boolean ativo) {
        Parametros.get().put(ativo, "PAUTA","NAO_IMPORTAR_SE_NCM_NAO_EXISTIR");
    }

    public boolean isNaoImportarPautaSeAlgumNcmNaoExistir() {
        return Parametros.get().getBool(true, "PAUTA","NAO_IMPORTAR_SE_NCM_NAO_EXISTIR");
    }
    
    public static class OpcoesExperimentaisDeProduto {
        
        public static void setUnificacaoExperimental(boolean ativo) {
            Parametros.get().put(ativo, "UNIFICACAO_PRODUTO_EXPERIMENTAL");
        }
        
        public static boolean isUnificacaoExperimentalAtiva() {
            return Parametros.get().getBool(true, "UNIFICACAO_PRODUTO_EXPERIMENTAL");
        }
        
        public static void setImportacaoMercadologicoExperimental(boolean ativo) {
            Parametros.get().put(ativo, "IMPORTACAO_MERCADOLOGICO_EXPERIMENTAL");
        }
        
        public static boolean isImportacaoMercadologicoExperimentalAtiva() {
            return Parametros.get().getBool(false, "IMPORTACAO_MERCADOLOGICO_EXPERIMENTAL");
        }
        
        public static void setUnificarSomenteProdutosComForcarNovo(boolean ativo) {
            Parametros.get().put(ativo, "UNIFICAR_SOMENTE_PRODUTOS_COM_FORCAR_NOVO");
        }
        
        public static boolean isUnificarSomenteProdutosComForcarNovo() {
            return Parametros.get().getBool(false, "UNIFICAR_SOMENTE_PRODUTOS_COM_FORCAR_NOVO");
        }

        public static boolean isIncluirProdutosNaoExistentes() {
            return Parametros.get().getBool(true, "UNIFICAR_INCLUIR_PRODUTOS_NOVOS");
        }

        public static void setIncluirProdutosNaoExistentes(boolean ativo) {
            Parametros.get().put(ativo, "UNIFICAR_INCLUIR_PRODUTOS_NOVOS");
        }
        
    }
   
}