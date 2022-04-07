/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.cadastro.pdv.promocao;

import java.util.Date;

/**
 *
 * @author lucasrafael
 */
public class PromocaoVO {

    private String id;
    private int idLoja;
    private String descricao;
    private Date dataInicio = new Date();
    private Date dataTermino = new Date();
    private int pontuacao;
    private double quantidade;
    private int qtdcupom;
    private int idSituacaocadastro = 1;
    private int idTipopromocao;
    private double valor;
    private int controle;
    private int idTipopercentualvalor;
    private int idTipoquantidade;
    private boolean aplicatodos = false;
    private String cupom;
    private double valordesconto;
    private boolean valorReferenteItensLista = false;
    private boolean verificaProdutosAuditados = false;
    private Date dataLimiteResgateCupom;
    private int idTipoPercentualValorDesconto;
    private double valorPaga;
    private boolean desconsiderarItem = false;
    private int qtdLimite;
    private boolean somenteClubeVantagens = false;
    private int diasExpiracao;
    private boolean utilizaQuantidadeProporcional = false;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the idLoja
     */
    public int getIdLoja() {
        return idLoja;
    }

    /**
     * @param idLoja the idLoja to set
     */
    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the dataInicio
     */
    public Date getDataInicio() {
        return dataInicio;
    }

    /**
     * @param dataInicio the dataInicio to set
     */
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    /**
     * @return the dataTermino
     */
    public Date getDataTermino() {
        return dataTermino;
    }

    /**
     * @param dataTermino the dataTermino to set
     */
    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    /**
     * @return the pontuacao
     */
    public int getPontuacao() {
        return pontuacao;
    }

    /**
     * @param pontuacao the pontuacao to set
     */
    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    /**
     * @return the quantidade
     */
    public double getQuantidade() {
        return quantidade;
    }

    /**
     * @param quantidade the quantidade to set
     */
    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * @return the qtdcupom
     */
    public int getQtdcupom() {
        return qtdcupom;
    }

    /**
     * @param qtdcupom the qtdcupom to set
     */
    public void setQtdcupom(int qtdcupom) {
        this.qtdcupom = qtdcupom;
    }

    /**
     * @return the idSituacaocadastro
     */
    public int getIdSituacaocadastro() {
        return idSituacaocadastro;
    }

    /**
     * @param idSituacaocadastro the idSituacaocadastro to set
     */
    public void setIdSituacaocadastro(int idSituacaocadastro) {
        this.idSituacaocadastro = idSituacaocadastro;
    }

    /**
     * @return the idTipopromocao
     */
    public int getIdTipopromocao() {
        return idTipopromocao;
    }

    /**
     * @param idTipopromocao the idTipopromocao to set
     */
    public void setIdTipopromocao(int idTipopromocao) {
        this.idTipopromocao = idTipopromocao;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the controle
     */
    public int getControle() {
        return controle;
    }

    /**
     * @param controle the controle to set
     */
    public void setControle(int controle) {
        this.controle = controle;
    }

    /**
     * @param controle the controle to set
     */
    public void setControle(String controle) {
        this.controle = Integer.parseInt(controle);
    }

    /**
     * @return the idTipopercentualvalor
     */
    public int getIdTipopercentualvalor() {
        return idTipopercentualvalor;
    }

    /**
     * @param idTipopercentualvalor the idTipopercentualvalor to set
     */
    public void setIdTipopercentualvalor(int idTipopercentualvalor) {
        this.idTipopercentualvalor = idTipopercentualvalor;
    }

    /**
     * @return the idTipoquantidade
     */
    public int getIdTipoquantidade() {
        return idTipoquantidade;
    }

    /**
     * @param idTipoquantidade the idTipoquantidade to set
     */
    public void setIdTipoquantidade(int idTipoquantidade) {
        this.idTipoquantidade = idTipoquantidade;
    }

    /**
     * @return the aplicatodos
     */
    public boolean isAplicatodos() {
        return aplicatodos;
    }

    /**
     * @param aplicatodos the aplicatodos to set
     */
    public void setAplicatodos(boolean aplicatodos) {
        this.aplicatodos = aplicatodos;
    }

    /**
     * @return the cupom
     */
    public String getCupom() {
        return cupom;
    }

    /**
     * @param cupom the cupom to set
     */
    public void setCupom(String cupom) {
        this.cupom = cupom;
    }

    /**
     * @return the valordesconto
     */
    public double getValordesconto() {
        return valordesconto;
    }

    /**
     * @param valordesconto the valordesconto to set
     */
    public void setValordesconto(double valordesconto) {
        this.valordesconto = valordesconto;
    }

    /**
     * @return the valorReferenteItensLista
     */
    public boolean isValorReferenteItensLista() {
        return valorReferenteItensLista;
    }

    /**
     * @param valorReferenteItensLista the valorReferenteItensLista to set
     */
    public void setValorReferenteItensLista(boolean valorReferenteItensLista) {
        this.valorReferenteItensLista = valorReferenteItensLista;
    }

    /**
     * @return the verificaProdutosAuditados
     */
    public boolean isVerificaProdutosAuditados() {
        return verificaProdutosAuditados;
    }

    /**
     * @param verificaProdutosAuditados the verificaProdutosAuditados to set
     */
    public void setVerificaProdutosAuditados(boolean verificaProdutosAuditados) {
        this.verificaProdutosAuditados = verificaProdutosAuditados;
    }

    /**
     * @return the dataLimiteResgateCupom
     */
    public Date getDataLimiteResgateCupom() {
        return dataLimiteResgateCupom;
    }

    /**
     * @param dataLimiteResgateCupom the dataLimiteResgateCupom to set
     */
    public void setDataLimiteResgateCupom(Date dataLimiteResgateCupom) {
        this.dataLimiteResgateCupom = dataLimiteResgateCupom;
    }

    /**
     * @return the idTipoPercentualValorDesconto
     */
    public int getIdTipoPercentualValorDesconto() {
        return idTipoPercentualValorDesconto;
    }

    /**
     * @param idTipoPercentualValorDesconto the idTipoPercentualValorDesconto to
     * set
     */
    public void setIdTipoPercentualValorDesconto(int idTipoPercentualValorDesconto) {
        this.idTipoPercentualValorDesconto = idTipoPercentualValorDesconto;
    }

    /**
     * @return the valorPaga
     */
    public double getValorPaga() {
        return valorPaga;
    }

    /**
     * @param valorPaga the valorPaga to set
     */
    public void setValorPaga(double valorPaga) {
        this.valorPaga = valorPaga;
    }

    /**
     * @return the desconsiderarItem
     */
    public boolean isDesconsiderarItem() {
        return desconsiderarItem;
    }

    /**
     * @param desconsiderarItem the desconsiderarItem to set
     */
    public void setDesconsiderarItem(boolean desconsiderarItem) {
        this.desconsiderarItem = desconsiderarItem;
    }

    /**
     * @return the qtdLimite
     */
    public int getQtdLimite() {
        return qtdLimite;
    }

    /**
     * @param qtdLimite the qtdLimite to set
     */
    public void setQtdLimite(int qtdLimite) {
        this.qtdLimite = qtdLimite;
    }

    /**
     * @return the somenteClubeVantagens
     */
    public boolean isSomenteClubeVantagens() {
        return somenteClubeVantagens;
    }

    /**
     * @param somenteClubeVantagens the somenteClubeVantagens to set
     */
    public void setSomenteClubeVantagens(boolean somenteClubeVantagens) {
        this.somenteClubeVantagens = somenteClubeVantagens;
    }

    /**
     * @return the diasExpiracao
     */
    public int getDiasExpiracao() {
        return diasExpiracao;
    }

    /**
     * @param diasExpiracao the diasExpiracao to set
     */
    public void setDiasExpiracao(int diasExpiracao) {
        this.diasExpiracao = diasExpiracao;
    }

    public boolean isUtilizaQuantidadeProporcional() {
        return utilizaQuantidadeProporcional;
    }

    public void setUtilizaQuantidadeProporcional(boolean utilizaQuantidadeProporcional) {
        this.utilizaQuantidadeProporcional = utilizaQuantidadeProporcional;
    }
}
