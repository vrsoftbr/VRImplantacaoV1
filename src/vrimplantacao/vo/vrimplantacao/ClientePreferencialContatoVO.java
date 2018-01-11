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
public class ClientePreferencialContatoVO {
    private int idClientePreferencial = 0;
    private String nome = "";
    private String telefone = "";
    private int idTipocontato = 0;
    private String email = "";
    private String celular = "";
    private double idClientePreferencialAnterior = 0;
  
    public void setIdClientePreferencial(int idClientePreferencial) {
        this.idClientePreferencial = idClientePreferencial;
    }
    
    public void setNome(String nome) {
        if (nome.length() > 40) {
            nome = nome.substring(0, 40);
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
        this.email = email;
    }
    
    public void setCelular(String celular) {
        if (celular.length() > 14) {
            celular = celular.substring(0, 14);
        }
        this.celular = celular;
    }
    
    public void setIdClientePreferencialAnterior(double idClientePreferencialAnterior) {
        this.idClientePreferencialAnterior = idClientePreferencialAnterior;
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
    
    public int getIdTipoContato() {
        return idTipocontato;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getCelular() {
        return celular;
    }
    
    public double getIdClientePreferncialAnterior() {
        return idClientePreferencialAnterior;
    }
}