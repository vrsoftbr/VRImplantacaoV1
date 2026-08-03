package vrimplantacao2.vo.cadastro.produtosimilar;

import java.util.Map;
import java.util.HashMap;

/**
 * Classe que representa o produto similar no banco de dados.
 * @author Wesley
 */
public class ProdutoSimilarVO {
    
    private int id;
    private String descricao;
    private int idSituacaoCadastro;
    private final Map<Integer, ProdutoSimilarItemVO> itens = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }
    
    public Map<Integer, ProdutoSimilarItemVO> getItens() {
        return itens;
    }
}
