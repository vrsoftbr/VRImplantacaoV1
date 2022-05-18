/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.desmembramento;

/**
 *
 * @author Alan
 */
public class DesmembramentoVO {
    
    private String id;
    private String id_produtopai;
    private String id_produtofilho;
    private int idSituacaocadastro = 1;
    private double estoque;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId_produtopai() {
        return id_produtopai;
    }
    
    public void setId_produtopai(String id_produtopai) {
        this.id_produtopai = id_produtopai;
    }
    
    public String getId_produtofilho() {
        return id_produtofilho;
    }
    
    public void setId_produtofilho(String id_produtofilho) {
        this.id_produtofilho = id_produtofilho;
    }
    
    public int getIdSituacaocadastro() {
        return idSituacaocadastro;
    }

    public void setIdSituacaocadastro(int idSituacaocadastro) {
        this.idSituacaocadastro = idSituacaocadastro;
    }
    
    public double getEstoque() {
        return estoque;
    }
    
    public void setEstoque(double estoque){
        this.estoque = estoque;
    }
}
