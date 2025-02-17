package vrimplantacao2.vo.cadastro.cliente;

import vrimplantacao.utils.Utils;

/**
 * Classe que representa os dependentes dos clientes preferênciais no VR.
 * @author Wesley
 */
public class ClientePreferencialDependenteVO {
    private int id;
    private int idClientePreferencial;
    private String nome = "SEM NOME";
    private Long cpf;
    private String tipoDependente = "";

    public int getId() {
        return id;
    }

    public int getIdClientePreferencial() {
        return idClientePreferencial;
    }

    public String getNome() {
        return nome;
    }

    public long getCpf() {
        return cpf;
    }

    public String getTipoDependente() {
        return tipoDependente;
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

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public void setTipoDependente(String tipoDependente) {
        this.tipoDependente = tipoDependente;
    }
}
