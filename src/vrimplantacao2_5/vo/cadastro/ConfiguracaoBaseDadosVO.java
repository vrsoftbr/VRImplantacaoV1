package vrimplantacao2_5.vo.cadastro;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosVO {
    
    private int id;
    private String descricao;
    private String host;
    private int porta;
    private String schema;
    private String usuario;
    private String senha;
    private ConfiguracaoBancoLojaVO configuracaoBancoLoja;
    private BancoDadosVO bancoDados;
    private SistemaVO sistema;
    private String complemento = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema.replace("'\'", "/");
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ConfiguracaoBancoLojaVO getConfiguracaoBancoLoja() {
        return configuracaoBancoLoja;
    }

    public void setConfiguracaoBancoLoja(ConfiguracaoBancoLojaVO configuracaoBancoLoja) {
        this.configuracaoBancoLoja = configuracaoBancoLoja;
    }

    public BancoDadosVO getBancoDados() {
        return bancoDados;
    }

    public void setBancoDados(BancoDadosVO bancoDados) {
        this.bancoDados = bancoDados;
    }

    public SistemaVO getSistema() {
        return sistema;
    }

    public void setSistema(SistemaVO sistema) {
        this.sistema = sistema;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
}
