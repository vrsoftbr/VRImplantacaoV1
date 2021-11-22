/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum EOpcoesMigracaoSistema {

    GATEWAYSISTEMAS(198, 5, false, true, true, true, true, true, false, true, false, false, false);

    private int idSistema;
    private int idBanco;
    private boolean migrarFamiliaProduto;
    private boolean migrarMercadologico;
    private boolean migrarProduto;
    private boolean migrarFornecedor;
    private boolean migrarProdutoFornecedor;
    private boolean migrarClientePreferencial;
    private boolean migrarClienteEventual;
    private boolean migrarCreditoRotativo;
    private boolean migrarCheque;
    private boolean migrarVenda;
    private boolean migrarContasPagar;

    EOpcoesMigracaoSistema(int idSistema, int idBanco,
            boolean migrarFamiliaProduto, boolean migrarMercadologico,
            boolean migrarProduto, boolean migrarFornecedor,
            boolean migrarProdutoFornecedor, boolean migrarClientePreferencial,
            boolean migrarClienteEventual, boolean migrarCreditoRotativo,
            boolean migrarCheque, boolean migrarVenda, boolean migrarContasPagar) {

        this.idSistema = idSistema;
        this.idBanco = idBanco;
        this.migrarFamiliaProduto = migrarFamiliaProduto;
        this.migrarMercadologico = migrarMercadologico;
        this.migrarProduto = migrarProduto;
        this.migrarFornecedor = migrarFornecedor;
        this.migrarProdutoFornecedor = migrarProdutoFornecedor;
        this.migrarClientePreferencial = migrarClientePreferencial;
        this.migrarClienteEventual = migrarClienteEventual;
        this.migrarCreditoRotativo = migrarCreditoRotativo;
        this.migrarCheque = migrarCheque;
        this.migrarVenda = migrarVenda;
        this.migrarContasPagar = migrarContasPagar;
    }

    public int getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(int idSistema) {
        this.idSistema = idSistema;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public boolean isMigrarFamiliaProduto() {
        return migrarFamiliaProduto;
    }

    public void setMigrarFamiliaProduto(boolean migrarFamiliaProduto) {
        this.migrarFamiliaProduto = migrarFamiliaProduto;
    }

    public boolean isMigrarMercadologico() {
        return migrarMercadologico;
    }

    public void setMigrarMercadologico(boolean migrarMercadologico) {
        this.migrarMercadologico = migrarMercadologico;
    }

    public boolean isMigrarProduto() {
        return migrarProduto;
    }

    public void setMigrarProduto(boolean migrarProduto) {
        this.migrarProduto = migrarProduto;
    }

    public boolean isMigrarFornecedor() {
        return migrarFornecedor;
    }

    public void setMigrarFornecedor(boolean migrarFornecedor) {
        this.migrarFornecedor = migrarFornecedor;
    }

    public boolean isMigrarProdutoFornecedor() {
        return migrarProdutoFornecedor;
    }

    public void setMigrarProdutoFornecedor(boolean migrarProdutoFornecedor) {
        this.migrarProdutoFornecedor = migrarProdutoFornecedor;
    }

    public boolean isMigrarClientePreferencial() {
        return migrarClientePreferencial;
    }

    public void setMigrarClientePreferencial(boolean migrarClientePreferencial) {
        this.migrarClientePreferencial = migrarClientePreferencial;
    }

    public boolean isMigrarClienteEventual() {
        return migrarClienteEventual;
    }

    public void setMigrarClienteEventual(boolean migrarClienteEventual) {
        this.migrarClienteEventual = migrarClienteEventual;
    }

    public boolean isMigrarCreditoRotativo() {
        return migrarCreditoRotativo;
    }

    public void setMigrarCreditoRotativo(boolean migrarCreditoRotativo) {
        this.migrarCreditoRotativo = migrarCreditoRotativo;
    }

    public boolean isMigrarCheque() {
        return migrarCheque;
    }

    public void setMigrarCheque(boolean migrarCheque) {
        this.migrarCheque = migrarCheque;
    }

    public boolean isMigrarVenda() {
        return migrarVenda;
    }

    public void setMigrarVenda(boolean migrarVenda) {
        this.migrarVenda = migrarVenda;
    }

    public boolean isMigrarContasPagar() {
        return migrarContasPagar;
    }

    public void setMigrarContasPagar(boolean migrarContasPagar) {
        this.migrarContasPagar = migrarContasPagar;
    }

    public static EOpcoesMigracaoSistema getByIdSistemaBanco(int idSistema, int idBanco) {
        for (EOpcoesMigracaoSistema opt : values()) {
            if (opt.getIdSistema() == idSistema && opt.getIdBanco() == idBanco) {
                return opt;
            }
        }
        return null;
    }
}
