package vr.implantacao.vo.cadastro;

/**
 *
 * @author guilhermegomes
 */
public class ConexaoVO {
    
    private int id;
    private String host;
    private int porta;
    private String usuario;
    private String senha;
    private String descricao;
    private int idSistemaBancoDados;

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

    public int getIdSistemaBancoDados() {
        return idSistemaBancoDados;
    }

    public void setIdSistemaBancoDados(int idSistemaBancoDados) {
        this.idSistemaBancoDados = idSistemaBancoDados;
    }
}
