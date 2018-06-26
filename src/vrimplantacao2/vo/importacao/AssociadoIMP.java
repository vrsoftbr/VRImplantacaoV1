package vrimplantacao2.vo.importacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a importação do associado.
 * @author Leandro
 */
public class AssociadoIMP {
    
    private String id;
    private String descricao;
    private final List<AssociadoItemIMP> itens = new ArrayList<>();

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

    public List<AssociadoItemIMP> getItens() {
        return itens;
    }

    public boolean addItem(String descricao, String idProduto, int qtdEmbalagem) {
        return itens.add(new AssociadoItemIMP(descricao, idProduto, qtdEmbalagem));
    }
    
}
