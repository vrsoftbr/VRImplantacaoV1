package vrimplantacao2.vo.importacao;

/**
 * Classe utilizada para importar as informações de dependentes de cliente.
 * @author Wesley
 */
public class ClienteDependenteIMP {
    private ClienteIMP cliente;
    private String id;
    private String nome;
    private Long cpf;
    private String tipodependente;

    public String getId() {
        return id;
    }
    
    public ClienteIMP getCliente() {
        return cliente;
    }

    public String getNome() {
        return nome;
    }

    public Long getCpf() {
        return cpf;
    }

    public String getTipodependente() {
        return tipodependente;
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

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public void setTipodependente(String tipodependente) {
        this.tipodependente = tipodependente;
    }
}
