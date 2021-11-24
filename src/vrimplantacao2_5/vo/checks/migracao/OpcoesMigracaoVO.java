/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.checks.migracao;

/**
 *
 * @author Desenvolvimento
 */
public class OpcoesMigracaoVO {
    
    private boolean habilitarMigracaoFamiliaProduto = false;
    private boolean habilitarMigracaoMercadologicos = false;
    private boolean habilitarMigracaoProdutos = false;
    private boolean habilitarMigracaoFornecedores = false;
    private boolean habilitarMigracaoProdutosFornecedores = false;
    private boolean habilitarMigracaoClientesPreferenciais = false;
    private boolean habilitarMigracaoClientesEventuais = false;
    private boolean habilitarMigracaoReceberCreditoRotativo = false;
    private boolean habilitarMigracaoReceberCheque = false;
    private boolean habilitarMigracaoVendas = false;
    private boolean habilitarMigracaoContasPagar = false;

    public boolean isHabilitarMigracaoFamiliaProduto() {
        return this.habilitarMigracaoFamiliaProduto;
    }
    
    public void setHabilitarMigracaoFamiliaProduto(boolean habilitarMigracaoFamiliaProduto) {
        this.habilitarMigracaoFamiliaProduto = habilitarMigracaoFamiliaProduto;
    }
    
    public boolean isHabilitarMigracaoMercadologicos() {
        return this.habilitarMigracaoMercadologicos;
    }
    
    public void setHabilitarMigracaoMercadologicos(boolean habilitarMigracaoMercadologico) {
        this.habilitarMigracaoMercadologicos = habilitarMigracaoMercadologico;
    }
    
    public boolean isHabilitarMigracaoProdutos() {
        return this.habilitarMigracaoProdutos;
    }
    
    public void setHabilitarMigracaoProdutos(boolean habilitarMigracaoProduto) {
        this.habilitarMigracaoProdutos = habilitarMigracaoProduto;
    }
    
    public boolean isHabilitarMigracaoFornecedores() {
        return this.habilitarMigracaoFornecedores;
    }
    
    public void setHabilitarMigracaoFornecedores(boolean habilitarMigracaoFornecedor) {
        this.habilitarMigracaoFornecedores = habilitarMigracaoFornecedor;
    }
    
    public boolean isHabilitarMigracaoProdutosFornecedores() {
        return this.habilitarMigracaoProdutosFornecedores;
    }
    
    public void setHabilitarMigracaoProdutosFornecedores(boolean habilitarMigracaoProdutoFornecedor) {
        this.habilitarMigracaoProdutosFornecedores = habilitarMigracaoProdutoFornecedor;
    }
    
    public boolean isHabilitarMigracaoClientesPreferenciais() {
        return this.habilitarMigracaoClientesPreferenciais;
    }
    
    public void setHabilitarMigracaoClientesPreferenciais(boolean habilitarMigracaoClientePreferencial) {
        this.habilitarMigracaoClientesPreferenciais = habilitarMigracaoClientePreferencial;
    }
    
    public boolean isHabilitarMigracaoClientesEventuais() {
        return this.habilitarMigracaoClientesEventuais;
    }
    
    public void setHabilitarMigracaoClientesEventuais(boolean habilitarMigracaoClienteEventual) {
        this.habilitarMigracaoClientesEventuais = habilitarMigracaoClienteEventual;
    }
    
    public boolean isHabilitarMigracaoReceberCreditoRotativo() {
        return this.habilitarMigracaoReceberCreditoRotativo;
    }
    
    public void setHabilitarMigracaoReceberCreditoRotativo(boolean habilitarMigracaoReceberCreditoRotativo) {
        this.habilitarMigracaoReceberCreditoRotativo = habilitarMigracaoReceberCreditoRotativo;
    }
    
    public boolean isHabilitarMigracaoReceberCheque() {
        return this.habilitarMigracaoReceberCheque;
    }
    
    public void setHabilitarMigracaoReceberCheque(boolean habilitarMigracaoReceberCheque) {
        this.habilitarMigracaoReceberCheque = habilitarMigracaoReceberCheque;
    }
    
    public boolean isHabilitarMigracaoVendas() {
        return this.habilitarMigracaoVendas;
    }
    
    public void setHabilitarMigracaoVendas(boolean habilitarMigracaoVenda) {
        this.habilitarMigracaoVendas = habilitarMigracaoVenda;
    }
    
    public boolean isHabilitarMigracaoContasPagar() {
        return this.habilitarMigracaoContasPagar;
    }
    
    public void setHabilitarMigracaoContasPagar(boolean habilitarMigracaoContaPagar) {
        this.habilitarMigracaoContasPagar = habilitarMigracaoContaPagar;
    }    
}
