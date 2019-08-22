package vrimplantacao2.vo.cadastro.divisao;

import vrimplantacao.utils.Utils;

/**
 * Representa um registro da tabela public.divisaofornecedor.
 * @author Leandro
 */
public class DivisaoFornecedorVO {
    
    private int id;
    private String descricao;

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
        this.descricao = Utils.acertarTexto(descricao, 15);
    }
    
}
