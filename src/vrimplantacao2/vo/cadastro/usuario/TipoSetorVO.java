package vrimplantacao2.vo.cadastro.usuario;

import vrimplantacao.utils.Utils;

public class TipoSetorVO {
    
    private int id = -1;
    private String descricao = "SEM DESCRICAO";

    public TipoSetorVO() {}

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
        this.descricao = Utils.acertarTexto(descricao, 20, "SEM DESCRICAO " + getId());
    }
}
