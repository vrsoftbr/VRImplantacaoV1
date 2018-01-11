/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

/**
 *
 * @author lucasrafael
 */
public class AgendaTelefoneVO {
    private int id = 0;
    private int id_loja = 1;
    private String nome = "";
    private String empresa = "";
    private String telefone = "";
    private int id_tipotelefone = 1;
    private int id_usuario = 0;
    private String email = "";
    private double idFornecedor = 0;
    private double idCliente = 0;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the id_loja
     */
    public int getId_loja() {
        return id_loja;
    }

    /**
     * @param id_loja the id_loja to set
     */
    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the empresa
     */
    public String getEmpresa() {
        return empresa;
    }

    /**
     * @param empresa the empresa to set
     */
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    /**
     * @return the telefone
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * @param telefone the telefone to set
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * @return the id_tipotelefone
     */
    public int getId_tipotelefone() {
        return id_tipotelefone;
    }

    /**
     * @param id_tipotelefone the id_tipotelefone to set
     */
    public void setId_tipotelefone(int id_tipotelefone) {
        this.id_tipotelefone = id_tipotelefone;
    }

    /**
     * @return the id_usuario
     */
    public int getId_usuario() {
        return id_usuario;
    }

    /**
     * @param id_usuario the id_usuario to set
     */
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the idFornecedor
     */
    public double getIdFornecedor() {
        return idFornecedor;
    }

    /**
     * @param idFornecedor the idFornecedor to set
     */
    public void setIdFornecedor(double idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    /**
     * @return the idCliente
     */
    public double getIdCliente() {
        return idCliente;
    }

    /**
     * @param idCliente the idCliente to set
     */
    public void setIdCliente(double idCliente) {
        this.idCliente = idCliente;
    }
}
