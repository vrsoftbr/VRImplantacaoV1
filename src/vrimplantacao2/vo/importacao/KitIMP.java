package vrimplantacao2.vo.importacao;

/**
 * Representa a importação do associado.
 *
 * @author Wesley
 */
public class KitIMP {

    private String impid_kit;
    private String impid_produto_principal;
    private boolean preco_normal;
    private String impid_kit_item;
    private String impid_produto_item;
    private double preco;
    private double quantidade;
    private int lojaAtual = 1;

    public String getImpid_kit() {
        return impid_kit;
    }

    public void setImpid_kit(String impid_kit) {
        this.impid_kit = impid_kit;
    }

    public String getImpid_produto_principal() {
        return impid_produto_principal;
    }

    public void setImpid_produto_principal(String impid_produto_principal) {
        this.impid_produto_principal = impid_produto_principal;
    }

    public boolean isPreco_normal() {
        return preco_normal;
    }

    public void setPreco_normal(boolean preco_normal) {
        this.preco_normal = preco_normal;
    }

    public String getImpid_kit_item() {
        return impid_kit_item;
    }

    public void setImpid_kit_item(String impid_kit_item) {
        this.impid_kit_item = impid_kit_item;
    }

    public String getImpid_produto_item() {
        return impid_produto_item;
    }

    public void setImpid_produto_item(String impid_produto_item) {
        this.impid_produto_item = impid_produto_item;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public int getLojaAtual() {
        return lojaAtual;
    }

    public void setLojaAtual(int lojaAtual) {
        this.lojaAtual = lojaAtual;
    } 
}
