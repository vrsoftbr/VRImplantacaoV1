package vrimplantacao2_5.vo.cadastro;

/**
 *
 * @author Desenvolvimento
 */
public class SistemaBancoDadosVO {

    private int id;
    private int idSistema;
    private String nomeSistema;
    private int idBancoDados;
    private String nomeBancoDados;
    private String nomeSchema;
    private String usuario;
    private String senha;
    private int porta;
    private String observacao;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getIdSistema() {
        return this.idSistema;
    }

    public String getNomeSistema() {
        return this.nomeSistema;
    }
    
    public int getIdBancoDados() {
        return this.idBancoDados;
    }
    
    public String getNomeBancoDados() {
        return this.nomeBancoDados;
    }
    
    public String getNomeSchema() {
        return this.nomeSchema;
    }
    
    public String getUsuario() {
        return this.usuario;
    }
    
    public String getSenha() {
        return this.senha;
    }
    
    public int getPorta() {
        return this.porta;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setIdSistema(int idSistema) {
        this.idSistema = idSistema;
    }
    
    public void setNomeSistema(String nomeSistema) {
        this.nomeSistema = nomeSistema;
    }
    
    public void setIdBancoDados(int idBancoDados) {
        this.idBancoDados = idBancoDados;
    }
    
    public void setNomeBancoDados(String nomeBancoDados) {
        this.nomeBancoDados = nomeBancoDados;
    }
    
    public void setNomeSchema(String nomeSchema) {
        this.nomeSchema = nomeSchema;
    }
    
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public void setPorta(int porta) {
        this.porta = porta;
    }
}
