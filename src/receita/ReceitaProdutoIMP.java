package receita;

import java.util.Objects;

/**
 * Várias receitas podem produzir o mesmo produto, esta classe faz esse vinculo e
 * retorna o rendimento.
 * @author leandro
 */
public class ReceitaProdutoIMP {
    
    private final ReceitaIMP receita;
    private String idProduto;
    private double rendimento;

    public ReceitaProdutoIMP(ReceitaIMP receita) {
        this.receita = receita;
    }

    public ReceitaProdutoIMP(ReceitaIMP receita, String idProduto, double rendimento) {
        this(receita);
        this.idProduto = idProduto;
        this.rendimento = rendimento;
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

    public double getRendimento() {
        return rendimento;
    }

    public void setRendimento(double rendimento) {
        this.rendimento = rendimento;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.receita);
        hash = 67 * hash + Objects.hashCode(this.idProduto);
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
        final ReceitaProdutoIMP other = (ReceitaProdutoIMP) obj;
        if (!Objects.equals(this.receita, other.receita)) {
            return false;
        }
        return Objects.equals(this.idProduto, other.idProduto);
    }
    
}
