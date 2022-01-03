/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.sistema;

/**
 *
 * @author Michael
 */
public class FXSistemasVO {

    private boolean temArquivoBalanca = false;
    private boolean produtosBalancaIniciaCom20 = false;
    private boolean produtosBalancaIniciaCom789 = false;

    public boolean isTemArquivoBalanca() {
        return this.temArquivoBalanca;
    }

    public boolean isProdutosBalancaIniciaCom20() {
        return this.produtosBalancaIniciaCom20;
    }
    
    public boolean isProdutosBalancaIniciaCom789() {
        return this.produtosBalancaIniciaCom789;
    }

    public void setTemArquivoBalanca(boolean temArquivoBalanca) {
        this.temArquivoBalanca = temArquivoBalanca;
    }

    public void setProdutosBalancaIniciaCom20(boolean produtosBalancaIniciaCom20) {
        this.produtosBalancaIniciaCom20 = produtosBalancaIniciaCom20;
    }
    
    public void setProdutosBalancaIniciaCom789(boolean produtosBalancaIniciaCom789) {
        this.produtosBalancaIniciaCom789 = produtosBalancaIniciaCom789;
    }
}
