/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.importacao;

import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author lucasrafael
 */
public class ReceitaIMP {

  private String importsistema;
  private String importid;
  private String importloja;
  private String idproduto;
  private String descricao;
  private SituacaoCadastro id_situacaocadastro = SituacaoCadastro.ATIVO;  
  private String fichatecnica;  
  private double rendimento;
  private int qtdembalagemreceita;
  private int qtdembalagemproduto;

    /**
     * @return the importid
     */
    public String getImportid() {
        return importid;
    }

    /**
     * @param importid the importid to set
     */
    public void setImportid(String importid) {
        this.importid = importid;
    }

    /**
     * @return the importloja
     */
    public String getImportloja() {
        return importloja;
    }

    /**
     * @param importloja the importloja to set
     */
    public void setImportloja(String importloja) {
        this.importloja = importloja;
    }

    /**
     * @return the idproduto
     */
    public String getIdproduto() {
        return idproduto;
    }

    /**
     * @param idproduto the idproduto to set
     */
    public void setIdproduto(String idproduto) {
        this.idproduto = idproduto;
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
     * @return the id_situacaocadastro
     */
    public SituacaoCadastro getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    /**
     * @param id_situacaocadastro the id_situacaocadastro to set
     */
    public void setId_situacaocadastro(SituacaoCadastro id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    /**
     * @return the fichatecnica
     */
    public String getFichatecnica() {
        return fichatecnica;
    }

    /**
     * @param fichatecnica the fichatecnica to set
     */
    public void setFichatecnica(String fichatecnica) {
        this.fichatecnica = fichatecnica;
    }

    /**
     * @return the rendimento
     */
    public double getRendimento() {
        return rendimento;
    }

    /**
     * @param rendimento the rendimento to set
     */
    public void setRendimento(double rendimento) {
        this.rendimento = rendimento;
    }

    /**
     * @return the qtdembalagemreceita
     */
    public int getQtdembalagemreceita() {
        return qtdembalagemreceita;
    }

    /**
     * @param qtdembalagemreceita the qtdembalagemreceita to set
     */
    public void setQtdembalagemreceita(int qtdembalagemreceita) {
        this.qtdembalagemreceita = qtdembalagemreceita;
    }

    /**
     * @return the qtdembalagemproduto
     */
    public int getQtdembalagemproduto() {
        return qtdembalagemproduto;
    }

    /**
     * @param qtdembalagemproduto the qtdembalagemproduto to set
     */
    public void setQtdembalagemproduto(int qtdembalagemproduto) {
        this.qtdembalagemproduto = qtdembalagemproduto;
    }

    /**
     * @return the importsistema
     */
    public String getImportsistema() {
        return importsistema;
    }

    /**
     * @param importsistema the importsistema to set
     */
    public void setImportsistema(String importsistema) {
        this.importsistema = importsistema;
    }

}
