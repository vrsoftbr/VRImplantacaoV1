package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;

public class LogEstoqueVO {
    private int  id_loja = 1;
    private int id_produto;
    private double quantidade;
    private int id_tipomovimentacao;
    private Date datahora;
    private int id_usuario;
    private String observacao;
    private double estoqueanterior;
    private double estoqueatual;
    private int id_tipoentradasaida;
    private double custosemimposto;
    private double custocomimposto;
    private Date datamovimento;
    private double customediocomimposto;
    private double customediosemimposto;
    private double estoqueanterior2;
    private double estoqueatual2;

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getId_produto() {
        return id_produto;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getId_tipomovimentacao() {
        return id_tipomovimentacao;
    }

    public void setId_tipomovimentacao(int id_tipomovimentacao) {
        this.id_tipomovimentacao = id_tipomovimentacao;
    }

    public Date getDatahora() {
        return datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public double getEstoqueanterior() {
        return estoqueanterior;
    }

    public void setEstoqueanterior(double estoqueanterior) {
        this.estoqueanterior = estoqueanterior;
    }

    public double getEstoqueatual() {
        return estoqueatual;
    }

    public void setEstoqueatual(double estoqueatual) {
        this.estoqueatual = estoqueatual;
    }

    public int getId_tipoentradasaida() {
        return id_tipoentradasaida;
    }

    public void setId_tipoentradasaida(int id_tipoentradasaida) {
        this.id_tipoentradasaida = id_tipoentradasaida;
    }

    public double getCustosemimposto() {
        return custosemimposto;
    }

    public void setCustosemimposto(double custosemimposto) {
        this.custosemimposto = custosemimposto;
    }

    public double getCustocomimposto() {
        return custocomimposto;
    }

    public void setCustocomimposto(double custocomimposto) {
        this.custocomimposto = custocomimposto;
    }

    public Date getDatamovimento() {
        return datamovimento;
    }

    public void setDatamovimento(Date datamovimento) {
        this.datamovimento = datamovimento;
    }

    public double getCustomediocomimposto() {
        return customediocomimposto;
    }

    public void setCustomediocomimposto(double customediocomimposto) {
        this.customediocomimposto = customediocomimposto;
    }

    public double getCustomediosemimposto() {
        return customediosemimposto;
    }

    public void setCustomediosemimposto(double customediosemimposto) {
        this.customediosemimposto = customediosemimposto;
    }

    public double getEstoqueanterior2() {
        return estoqueanterior2;
    }

    public void setEstoqueanterior2(double estoqueanterior2) {
        this.estoqueanterior2 = estoqueanterior2;
    }

    public double getEstoqueatual2() {
        return estoqueatual2;
    }

    public void setEstoqueatual2(double estoqueatual2) {
        this.estoqueatual2 = estoqueatual2;
    }    
}