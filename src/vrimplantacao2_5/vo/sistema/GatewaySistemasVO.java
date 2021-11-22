package vrimplantacao2_5.vo.sistema;

/**
 *
 * @author Desenvolvimento
 */
public class GatewaySistemasVO {

    private boolean temArquivoBalanca = false;
    private boolean produtosBalancaIniciaCom20 = false;
    private boolean migrarProdutosAtivos = false;   
    private boolean migrarFamiliaProduto = false;
    private boolean migrarMercadologicos = false;
    private boolean migrarProdutos = false;
    private boolean migrarFornecedores = false;
    private boolean migrarProdutosFornecedores = false;
    private boolean migrarClientesPreferenciais = false;
    private boolean migrarClientesEventuais = false;
    private boolean migrarReceberCreditoRotativo = false;
    private boolean migrarReceberCheque = false;
    private boolean migrarVendas = false;
    private boolean migrarContasPagar = false;

    public boolean getTemArquivoBalanca() {
        return this.temArquivoBalanca;
    }

    public boolean getProdutosBalancaIniciaCom20() {
        return this.produtosBalancaIniciaCom20;
    }

    public boolean getMigrarProdutosAtivos() {
        return this.migrarProdutosAtivos;
    }

    public void setTemArquivoBalanca(boolean temArquivoBalanca) {
        this.temArquivoBalanca = temArquivoBalanca;
    }

    public void setProdutosBalancaIniciaCom20(boolean produtosBalancaIniciaCom20) {
        this.produtosBalancaIniciaCom20 = produtosBalancaIniciaCom20;
    }

    public void setMigrarProdutosAtivo(boolean migrarProdutosAtivos) {
        this.migrarProdutosAtivos = migrarProdutosAtivos;
    }
    
    public boolean isMigrarFamiliaProduto() {
        return this.migrarFamiliaProduto;
    }
    
    public void setMigrarFamiliaProduto(boolean migrarFamiliaProduto) {
        this.migrarFamiliaProduto = migrarFamiliaProduto;
    }
    
    public boolean isMigrarMercadologicos() {
        return this.migrarMercadologicos;
    }
    
    public void setMigrarMercadologicos(boolean migrarMercadologico) {
        this.migrarMercadologicos = migrarMercadologico;
    }
    
    public boolean isMigrarProdutos() {
        return this.migrarProdutos;
    }
    
    public void setMigrarProdutos(boolean migrarProduto) {
        this.migrarProdutos = migrarProduto;
    }
    
    public boolean isMigrarFornecedores() {
        return this.migrarFornecedores;
    }
    
    public void setMigrarFornecedores(boolean migrarFornecedor) {
        this.migrarFornecedores = migrarFornecedor;
    }
    
    public boolean isMigrarProdutosFornecedores() {
        return this.migrarProdutosFornecedores;
    }
    
    public void setMigrarProdutosFornecedores(boolean migrarProdutoFornecedor) {
        this.migrarProdutosFornecedores = migrarProdutoFornecedor;
    }
    
    public boolean isMigrarClientesPreferenciais() {
        return this.migrarClientesPreferenciais;
    }
    
    public void setMigrarClientesPreferenciais(boolean migrarClientePreferencial) {
        this.migrarClientesPreferenciais = migrarClientePreferencial;
    }
    
    public boolean isMigrarClientesEventuais() {
        return this.migrarClientesEventuais;
    }
    
    public void setMigrarClientesEventuais(boolean migrarClienteEventual) {
        this.migrarClientesEventuais = migrarClienteEventual;
    }
    
    public boolean isMigrarReceberCreditoRotativo() {
        return this.migrarReceberCreditoRotativo;
    }
    
    public void setMigrarReceberCreditoRotativo(boolean migrarReceberCreditoRotativo) {
        this.migrarReceberCreditoRotativo = migrarReceberCreditoRotativo;
    }
    
    public boolean isMigrarReceberCheque() {
        return this.migrarReceberCheque;
    }
    
    public void setMigrarReceberCheque(boolean migrarReceberCheque) {
        this.migrarReceberCheque = migrarReceberCheque;
    }
    
    public boolean isMigrarVendas() {
        return this.migrarVendas;
    }
    
    public void setMigrarVendas(boolean migrarVenda) {
        this.migrarVendas = migrarVenda;
    }
    
    public boolean isMigrarContasPagar() {
        return this.migrarContasPagar;
    }
    
    public void setMigrarContasPagar(boolean migrarContaPagar) {
        this.migrarContasPagar = migrarContaPagar;
    }
    
}
