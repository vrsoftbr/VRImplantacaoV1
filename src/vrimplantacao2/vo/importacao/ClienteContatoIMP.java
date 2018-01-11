package vrimplantacao2.vo.importacao;

/**
 * Classe utilizada para importar as informações de contato de cliente.
 * @author Leandro
 */
public class ClienteContatoIMP {
    private ClienteIMP cliente;
    private String id;
    private String nome;
    private String telefone;
    private String celular;
    private String email;

    public String getId() {
        return id;
    }
    
    public ClienteIMP getCliente() {
        return cliente;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCelular() {
        return celular;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCliente(ClienteIMP cliente) {
        this.cliente = cliente;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
