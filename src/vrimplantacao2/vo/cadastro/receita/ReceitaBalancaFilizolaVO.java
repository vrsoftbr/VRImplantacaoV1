package vrimplantacao2.vo.cadastro.receita;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Leandro
 */
public class ReceitaBalancaFilizolaVO {
    
    private int id;
    private String descricao;// character varying(30) NOT NULL,
    private String receita;// character varying(280) NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;// id_situacaocadastro integer NOT NULL,

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
        this.descricao = Utils.acertarTexto(descricao, 30, "SEM DESCRIÇÃO");
    }

    public String getReceita() {
        return receita;
    }

    public void setReceita(String receita) {
        this.receita = Utils.acertarTextoMultiLinha(receita, 280, "").replace("?", "");
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }
    
    
    
}
