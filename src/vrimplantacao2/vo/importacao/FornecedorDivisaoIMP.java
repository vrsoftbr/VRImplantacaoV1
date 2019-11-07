/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.vo.importacao;

/**
 *
 * @author lucasrafael
 */
public class FornecedorDivisaoIMP {

    private String importSistema;
    private String importLoja;
    private String importFornecedorId;
    private String importId; 
    private int prazoVisita;
    private int prazoEntrega;
    private int prazoSeguranca;

    /**
     * @return the importSistema
     */
    public String getImportSistema() {
        return importSistema;
    }

    /**
     * @param importSistema the importSistema to set
     */
    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    /**
     * @return the importLoja
     */
    public String getImportLoja() {
        return importLoja;
    }

    /**
     * @param importLoja the importLoja to set
     */
    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }

    /**
     * @return the importFornecedorId
     */
    public String getImportFornecedorId() {
        return importFornecedorId;
    }

    /**
     * @param importFornecedorId the importFornecedorId to set
     */
    public void setImportFornecedorId(String importFornecedorId) {
        this.importFornecedorId = importFornecedorId;
    }

    /**
     * @return the importId
     */
    public String getImportId() {
        return importId;
    }

    /**
     * @param importId the importId to set
     */
    public void setImportId(String importId) {
        this.importId = importId;
    }

    /**
     * @return the prazoVisita
     */
    public int getPrazoVisita() {
        return prazoVisita;
    }

    /**
     * @param prazoVisita the prazoVisita to set
     */
    public void setPrazoVisita(int prazoVisita) {
        this.prazoVisita = prazoVisita;
    }

    /**
     * @return the prazoEntrega
     */
    public int getPrazoEntrega() {
        return prazoEntrega;
    }

    /**
     * @param prazoEntrega the prazoEntrega to set
     */
    public void setPrazoEntrega(int prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    /**
     * @return the prazoSeguranca
     */
    public int getPrazoSeguranca() {
        return prazoSeguranca;
    }

    /**
     * @param prazoSeguranca the prazoSeguranca to set
     */
    public void setPrazoSeguranca(int prazoSeguranca) {
        this.prazoSeguranca = prazoSeguranca;
    }
}
