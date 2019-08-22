package vrimplantacao2.vo.importacao;

import java.util.HashSet;
import java.util.Set;

/**
 * Receita de balança.
 * @author Leandro
 */
public class ReceitaBalancaIMP {
    
    private String id;
    private String descricao;
    private String receita;
    private Set<String> produtos = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getReceita() {
        return receita;
    }

    public void setReceita(String receita) {
        this.receita = receita;
    }

    public Set<String> getProdutos() {
        return produtos;
    }

    public boolean addProduto(String id) {
        return this.produtos.add(id);
    }
    
}
