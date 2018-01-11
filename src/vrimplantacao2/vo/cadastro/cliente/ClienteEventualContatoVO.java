package vrimplantacao2.vo.cadastro.cliente;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.TipoContato;

/**
 * Classe que representa os contatos dos clientes preferênciais no VR.
 * @author Leandro
 */
public class ClienteEventualContatoVO {
    private int id;
    private int idClienteEventual;
    private String nome = "SEM NOME";
    private String telefone = "";
    private String celular = "";
    private String email = "";
    private TipoContato tipoContato = TipoContato.COMERCIAL;

    public int getId() {
        return id;
    }

    public int getIdClienteEventual() {
        return idClienteEventual;
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

    public TipoContato getTipoContato() {
        return tipoContato;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdClienteEventual(int idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40, "SEM NOME");
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.stringLong(telefone);
    }

    public void setCelular(String celular) {
        this.celular = Utils.stringLong(celular);
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : "";
    }

    public void setTipoContato(TipoContato tipoContato) {
        this.tipoContato = tipoContato;
    }
    
    
}
