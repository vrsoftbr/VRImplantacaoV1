package receita;

import java.util.Objects;

/**
 *
 * @author leandro
 */
public class ReceitaItemIMP {

    private final ReceitaIMP receita;
    private String idProduto;
    private int qtdEmbalagemReceita = 1;
    private int qtdEmbalagemProduto = 1;
    private double fatorConversao = 1;
    private boolean baixaEstoque = true;
    private boolean embalagem = false;

    public ReceitaItemIMP(ReceitaIMP receita) {
        this.receita = receita;
    }

    public ReceitaIMP getReceita() {
        return receita;
    }
    
    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public int getQtdEmbalagemReceita() {
        return qtdEmbalagemReceita;
    }

    public void setQtdEmbalagemReceita(int qtdEmbalagemReceita) {
        this.qtdEmbalagemReceita = qtdEmbalagemReceita;
    }

    public int getQtdEmbalagemProduto() {
        return qtdEmbalagemProduto;
    }

    public void setQtdEmbalagemProduto(int qtdEmbalagemProduto) {
        this.qtdEmbalagemProduto = qtdEmbalagemProduto;
    }

    public double getFatorConversao() {
        return fatorConversao;
    }

    public void setFatorConversao(double fatorConversao) {
        this.fatorConversao = fatorConversao;
    }

    public boolean isBaixaEstoque() {
        return baixaEstoque;
    }

    public void setBaixaEstoque(boolean baixaEstoque) {
        this.baixaEstoque = baixaEstoque;
    }

    public boolean isEmbalagem() {
        return embalagem;
    }

    public void setEmbalagem(boolean embalagem) {
        this.embalagem = embalagem;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.receita);
        hash = 83 * hash + Objects.hashCode(this.idProduto);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReceitaItemIMP other = (ReceitaItemIMP) obj;
        if (!Objects.equals(this.receita, other.receita)) {
            return false;
        }
        if (!Objects.equals(this.idProduto, other.idProduto)) {
            return false;
        }
        return true;
    }
    
}
