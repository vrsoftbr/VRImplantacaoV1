package vrimplantacao2_5.vo.sistema;

/**
 *
 * @author Desenvolvimento
 */
public class GatewaySistemasVO {

    private boolean temArquivoBalanca = false;
    private boolean produtosBalancaIniciaCom20 = false;

    public boolean isTemArquivoBalanca() {
        return this.temArquivoBalanca;
    }

    public boolean isProdutosBalancaIniciaCom20() {
        return this.produtosBalancaIniciaCom20;
    }

    public void setTemArquivoBalanca(boolean temArquivoBalanca) {
        this.temArquivoBalanca = temArquivoBalanca;
    }

    public void setProdutosBalancaIniciaCom20(boolean produtosBalancaIniciaCom20) {
        this.produtosBalancaIniciaCom20 = produtosBalancaIniciaCom20;
    }

}
