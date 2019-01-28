package vrimplantacao2.vo.cadastro.cliente;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.TipoContato;

/**
 * Classe que representa os contatos dos clientes preferênciais no VR.
 * @author Leandro
 */
public class ClientePreferencialContatoVO {
    private int id;
    private int idClientePreferencial;
    private String nome = "SEM NOME";
    private String telefone = "";
    private String celular = "";
    private TipoContato tipoContato = TipoContato.COMERCIAL;

    public int getId() {
        return id;
    }

    public int getIdClientePreferencial() {
        return idClientePreferencial;
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

    public TipoContato getTipoContato() {
        return tipoContato;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdClientePreferencial(int idClientePreferencial) {
        this.idClientePreferencial = idClientePreferencial;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40, "SEM NOME");
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(Utils.stringLong(telefone), 14, "0000000000");
    }

    public void setCelular(String celular) {
        this.celular = Utils.formataNumero(Utils.stringLong(celular), 14, "");
    }

    public void setTipoContato(TipoContato tipoContato) {
        this.tipoContato = tipoContato;
    }
    
    
}
