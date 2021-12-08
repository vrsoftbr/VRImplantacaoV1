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

    GATEWAYSISTEMAS(198, 5, false, true, true, true, true, true, false, true, false, false, false),
    AVISTARE(16, 13, false, true, true, true, true, true, true, true, true, true, false);
    
    private int idSistema;
    private int idBanco;
    private boolean habilitarMigracaoFamiliaProduto;
    private boolean habilitarMigracaoMercadologico;
    private boolean habilitarMigracaoProduto;
    private boolean habilitarMigracaoFornecedor;
    private boolean habilitarMigracaoProdutoFornecedor;
    private boolean habilitarMigracaoClientePreferencial;
    private boolean habilitarMigracaoClienteEventual;
    private boolean habilitarMigracaoCreditoRotativo;
    private boolean habilitarMigracaoCheque;
    private boolean habilitarMigracaoVenda;
    private boolean habilitarMigracaoContasPagar;

    EOpcoesMigracaoSistema(int idSistema, int idBanco,
            boolean habilitarMigracaoFamiliaProduto, boolean habilitarMigracaoMercadologico,
            boolean habilitarMigracaoProduto, boolean habilitarMigracaoFornecedor,
            boolean habilitarMigracaoProdutoFornecedor, boolean habilitarMigracaoClientePreferencial,
            boolean habilitarMigracaoClienteEventual, boolean habilitarMigracaoCreditoRotativo,
            boolean habilitarMigracaoCheque, boolean habilitarMigracaoVenda, boolean habilitarMigracaoContasPagar) {

        this.idSistema = idSistema;
        this.idBanco = idBanco;
        this.habilitarMigracaoFamiliaProduto = habilitarMigracaoFamiliaProduto;
        this.habilitarMigracaoMercadologico = habilitarMigracaoMercadologico;
        this.habilitarMigracaoProduto = habilitarMigracaoProduto;
        this.habilitarMigracaoFornecedor = habilitarMigracaoFornecedor;
        this.habilitarMigracaoProdutoFornecedor = habilitarMigracaoProdutoFornecedor;
        this.habilitarMigracaoClientePreferencial = habilitarMigracaoClientePreferencial;
        this.habilitarMigracaoClienteEventual = habilitarMigracaoClienteEventual;
        this.habilitarMigracaoCreditoRotativo = habilitarMigracaoCreditoRotativo;
        this.habilitarMigracaoCheque = habilitarMigracaoCheque;
        this.habilitarMigracaoVenda = habilitarMigracaoVenda;
        this.habilitarMigracaoContasPagar = habilitarMigracaoContasPagar;
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

    public boolean isHabilitarMigracaoFamiliaProduto() {
        return habilitarMigracaoFamiliaProduto;
    }

    public void setHabilitarMigracaoFamiliaProduto(boolean habilitarMigracaoFamiliaProduto) {
        this.habilitarMigracaoFamiliaProduto = habilitarMigracaoFamiliaProduto;
    }

    public boolean isHabilitarMigracaoMercadologico() {
        return habilitarMigracaoMercadologico;
    }

    public void setHabilitarMigracaoMercadologico(boolean habilitarMigracaoMercadologico) {
        this.habilitarMigracaoMercadologico = habilitarMigracaoMercadologico;
    }

    public boolean isHabilitarMigracaoProduto() {
        return habilitarMigracaoProduto;
    }

    public void setHabilitarMigracaoProduto(boolean habilitarMigracaoProduto) {
        this.habilitarMigracaoProduto = habilitarMigracaoProduto;
    }

    public boolean isHabilitarMigracaoFornecedor() {
        return habilitarMigracaoFornecedor;
    }

    public void setHabilitarMigracaoFornecedor(boolean habilitarMigracaoFornecedor) {
        this.habilitarMigracaoFornecedor = habilitarMigracaoFornecedor;
    }

    public boolean isHabilitarMigracaoProdutoFornecedor() {
        return habilitarMigracaoProdutoFornecedor;
    }

    public void setHabilitarMigracaoProdutoFornecedor(boolean habilitarMigracaoProdutoFornecedor) {
        this.habilitarMigracaoProdutoFornecedor = habilitarMigracaoProdutoFornecedor;
    }

    public boolean isHabilitarMigracaoClientePreferencial() {
        return habilitarMigracaoClientePreferencial;
    }

    public void setHabilitarMigracaoClientePreferencial(boolean habilitarMigracaoClientePreferencial) {
        this.habilitarMigracaoClientePreferencial = habilitarMigracaoClientePreferencial;
    }

    public boolean isHabilitarMigracaoClienteEventual() {
        return habilitarMigracaoClienteEventual;
    }

    public void setHabilitarMigracaoClienteEventual(boolean habilitarMigracaoClienteEventual) {
        this.habilitarMigracaoClienteEventual = habilitarMigracaoClienteEventual;
    }

    public boolean isHabilitarMigracaoCreditoRotativo() {
        return habilitarMigracaoCreditoRotativo;
    }

    public void setHabilitarMigracaoCreditoRotativo(boolean habilitarMigracaoCreditoRotativo) {
        this.habilitarMigracaoCreditoRotativo = habilitarMigracaoCreditoRotativo;
    }

    public boolean isHabilitarMigracaoCheque() {
        return habilitarMigracaoCheque;
    }

    public void setHabilitarMigracaoCheque(boolean habilitarMigracaoCheque) {
        this.habilitarMigracaoCheque = habilitarMigracaoCheque;
    }

    public boolean isHabilitarMigracaoVenda() {
        return habilitarMigracaoVenda;
    }

    public void setHabilitarMigracaoVenda(boolean habilitarMigracaoVenda) {
        this.habilitarMigracaoVenda = habilitarMigracaoVenda;
    }

    public boolean isHabilitarMigracaoContasPagar() {
        return habilitarMigracaoContasPagar;
    }

    public void setHabilitarMigracaoContasPagar(boolean habilitarMigracaoContasPagar) {
        this.habilitarMigracaoContasPagar = habilitarMigracaoContasPagar;
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
