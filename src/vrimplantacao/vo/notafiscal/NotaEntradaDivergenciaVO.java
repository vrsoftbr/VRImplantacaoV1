package vrimplantacao.vo.notafiscal;

import java.io.Serializable;

public class NotaEntradaDivergenciaVO implements Serializable {

    public long id = 0;
    public int idLoja = 0;
    public long idNotaEntrada = 0;
    public int idProduto = 0;
    public String produto = "";
    public int idTipoDivergenciaEntrada = 0;
    public double quantidadeNota = 0;
    public double quantidadePedido = 0;
    public double custoNota = 0;
    public double custoPedido = 0;
    public double custoAnterior = 0;
    public String tipoDivergenciaEntrada = "";
    public int idTipoEmbalagem = 0;
    public double percentual = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public long getIdNotaEntrada() {
        return idNotaEntrada;
    }

    public void setIdNotaEntrada(long idNotaEntrada) {
        this.idNotaEntrada = idNotaEntrada;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public int getIdTipoDivergenciaEntrada() {
        return idTipoDivergenciaEntrada;
    }

    public void setIdTipoDivergenciaEntrada(int idTipoDivergenciaEntrada) {
        this.idTipoDivergenciaEntrada = idTipoDivergenciaEntrada;
    }

    public double getQuantidadeNota() {
        return quantidadeNota;
    }

    public void setQuantidadeNota(double quantidadeNota) {
        this.quantidadeNota = quantidadeNota;
    }

    public double getQuantidadePedido() {
        return quantidadePedido;
    }

    public void setQuantidadePedido(double quantidadePedido) {
        this.quantidadePedido = quantidadePedido;
    }

    public double getCustoNota() {
        return custoNota;
    }

    public void setCustoNota(double custoNota) {
        this.custoNota = custoNota;
    }

    public double getCustoPedido() {
        return custoPedido;
    }

    public void setCustoPedido(double custoPedido) {
        this.custoPedido = custoPedido;
    }

    public double getCustoAnterior() {
        return custoAnterior;
    }

    public void setCustoAnterior(double custoAnterior) {
        this.custoAnterior = custoAnterior;
    }

    public String getTipoDivergenciaEntrada() {
        return tipoDivergenciaEntrada;
    }

    public void setTipoDivergenciaEntrada(String tipoDivergenciaEntrada) {
        this.tipoDivergenciaEntrada = tipoDivergenciaEntrada;
    }

    public int getIdTipoEmbalagem() {
        return idTipoEmbalagem;
    }

    public void setIdTipoEmbalagem(int idTipoEmbalagem) {
        this.idTipoEmbalagem = idTipoEmbalagem;
    }

    public double getPercentual() {
        return percentual;
    }

    public void setPercentual(double percentual) {
        this.percentual = percentual;
    }
}