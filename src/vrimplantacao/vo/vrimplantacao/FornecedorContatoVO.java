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
public class FornecedorContatoVO {
    private int idFornecedor = 0;
    private String nome = "";
    private String telefone = "";
    private int idTipocontato = 0;
    private String email = "";
    private String celular = "";
    private String fax = "";
    private double idFornecedorAnterior = 0;
  
    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }
    
    public void setNome(String nome) {
        if (nome.length() > 30) {
            nome = nome.substring(0, 30);
        }
        this.nome = nome;
    }
    
    public void setTelefone(String telefone) {
        if (telefone.length() > 14) {
            telefone = telefone.substring(0, 14);
        }
        this.telefone = telefone;
    }
    
    public void setIdTipoContato(int idTipoContato) {
        this.idTipocontato = idTipoContato;
    }
    
    public void setEmail(String email) {
        if (email.length() > 50) {
            email = email.substring(0, 50);
        }
        this.email = email;
    }
    
    public void setCelular(String celular) {
        if (celular.length() > 14) {
            celular = celular.substring(0, 14);
        }
        this.celular = celular;
    }
    
    public void setIdFornecedorAnterior(double idFornecedorAnterior) {
        this.idFornecedorAnterior = idFornecedorAnterior;
    }
    
    public int getIdFornecedor() {
        return idFornecedor;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public int getIdTipoContato() {
        return idTipocontato;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getCelular() {
        return celular;
    }
    
    public double getIdFornecedorAnterior() {
        return idFornecedorAnterior;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        if (fax.length() > 14) {
            fax = fax.substring(0, 14);
        }
        this.fax = fax;
    }
}