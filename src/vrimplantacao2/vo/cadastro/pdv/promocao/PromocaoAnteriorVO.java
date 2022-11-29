/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.pdv.promocao;

import java.util.Date;

/**
 *
 * @author Michael
 */
public class PromocaoAnteriorVO {

    private String sistema;
    private String loja;
    private PromocaoVO codigoAtual;
    private String descricao;
    private Date dataInicio = new Date();
    private Date dataTermino = new Date();
    private String ean;
    private String id_produto;
    private String descricaoCompleta;
    private double quantidade;
    private double paga;
    private int idConexao;
    private String id_promocao;
    private int id_finalizadora;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getId_promocao() {
        return id_promocao;
    }

    public void setId_promocao(String id_promocao) {
        this.id_promocao = id_promocao;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public PromocaoVO getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(PromocaoVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getId_produto() {
        return id_produto;
    }

    public void setId_produto(String id_produto) {
        this.id_produto = id_produto;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPaga() {
        return paga;
    }

    public void setPaga(double paga) {
        this.paga = paga;
    }

    public int getIdConexao() {
        return this.idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }

    public int getId_finalizadora() {
        return id_finalizadora;
    }

    public void setId_finalizadora(int id_finalizadora) {
        this.id_finalizadora = id_finalizadora;
    }

    public void setCodigoatual(PromocaoVO codigoatual) {
        this.codigoAtual = codigoatual;
    }

    
}