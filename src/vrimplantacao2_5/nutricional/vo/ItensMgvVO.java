/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.vo;

/**
 *
 * @author Desenvolvimento
 */
public class ItensMgvVO {
    private String departamento = "";
    private String tipo = "";
    private int codigo = 0;
    private String preco = "";
    private int validade = 0;
    private String descricao = "";
    private int nutricional = 0;
    private String demaisDados = "";
    private String pesavel = "";

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public int getValidade() {
        return validade;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getNutricional() {
        return nutricional;
    }

    public void setNutricional(int nutricional) {
        this.nutricional = nutricional;
    }

    public String getDemaisDados() {
        return demaisDados;
    }

    public void setDemaisDados(String demaisDados) {
        this.demaisDados = demaisDados;
    }

    public String getPesavel() {
        return pesavel;
    }

    public void setPesavel(String pesavel) {
        this.pesavel = pesavel;
    }

    
}
