package vrimplantacao2.vo.cadastro.fornecedor;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class FamiliaFornecedorVO {
    
    private int id = -1;
    private String descricao = "SEM DESCRICAO";
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;

    public FamiliaFornecedorVO(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public FamiliaFornecedorVO() {}

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
        this.descricao = Utils.acertarTexto(descricao, 35, "SEM DESCRICAO " + getId());
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }
}
