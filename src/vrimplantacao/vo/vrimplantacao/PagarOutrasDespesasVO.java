/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lucasrafael
 */
public class PagarOutrasDespesasVO {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    public int id_fornecedor = 0;
    public int numerodocumento = 0;
    public int id_tipoentrada = 0;
    public String dataemissao = "";
    public String dataentrada = "";
    public double valor = 0;
    public int id_situacaopagaroutrasdespesas = 0;
    public int id_loja = 0;
    public String observacao = "";
    public int id_tipopiscofins = 1;
    public int id_tipobasecalculocredito = 0;   
    public List<PagarOutrasDespesasVencimentoVO> vPagarOutrasDespesasVencimento = new ArrayList();

    public int getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(int id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public int getNumerodocumento() {
        return numerodocumento;
    }

    public void setNumerodocumento(int numerodocumento) {
        this.numerodocumento = numerodocumento;
    }

    public int getId_tipoentrada() {
        return id_tipoentrada;
    }

    public void setId_tipoentrada(int id_tipoentrada) {
        this.id_tipoentrada = id_tipoentrada;
    }

    public String getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(String dataemissao) {
        this.dataemissao = dataemissao;
    }
    
    public void setDataemissao(Date dataemissao) {
        this.dataemissao = DATE_FORMAT.format(dataemissao);
    }

    public String getDataentrada() {
        return dataentrada;
    }

    public void setDataentrada(String dataentrada) {
        this.dataentrada = dataentrada;
    }
    
    public void setDataentrada(Date dataentrada) {
        this.dataentrada = DATE_FORMAT.format(dataentrada);
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getId_situacaopagaroutrasdespesas() {
        return id_situacaopagaroutrasdespesas;
    }

    public void setId_situacaopagaroutrasdespesas(int id_situacaopagaroutrasdespesas) {
        this.id_situacaopagaroutrasdespesas = id_situacaopagaroutrasdespesas;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getId_tipopiscofins() {
        return id_tipopiscofins;
    }

    public void setId_tipopiscofins(int id_tipopiscofins) {
        this.id_tipopiscofins = id_tipopiscofins;
    }

    public int getId_tipobasecalculocredito() {
        return id_tipobasecalculocredito;
    }

    public void setId_tipobasecalculocredito(int id_tipobasecalculocredito) {
        this.id_tipobasecalculocredito = id_tipobasecalculocredito;
    }

    public List<PagarOutrasDespesasVencimentoVO> getvPagarOutrasDespesasVencimento() {
        return vPagarOutrasDespesasVencimento;
    }

    public void setvPagarOutrasDespesasVencimento(List<PagarOutrasDespesasVencimentoVO> vPagarOutrasDespesasVencimento) {
        this.vPagarOutrasDespesasVencimento = vPagarOutrasDespesasVencimento;
    }
    
    
}
