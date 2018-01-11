package vrimplantacao2.vo.cadastro.oferta;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 * Tipo de uma oferta.
 * @author Leandro
 */
public class TipoOfertaVO {
    
    public static final TipoOfertaVO CAPA = new TipoOfertaVO(1, "CAPA");
    public static final TipoOfertaVO MERCEARIA = new TipoOfertaVO(2, "MERCEARIA");
    public static final TipoOfertaVO PERECIVEIS = new TipoOfertaVO(3, "PERECIVEIS");
    public static final TipoOfertaVO BEBIDAS = new TipoOfertaVO(4, "BEBIDAS");
    public static final TipoOfertaVO HIGIENE = new TipoOfertaVO(5, "HIGIENE");
    public static final TipoOfertaVO ULT_HORA = new TipoOfertaVO(6, "ULT_HORA");
    public static final TipoOfertaVO CONCORRENTE = new TipoOfertaVO(7, "CONCORRENTE");
    
    private int id;
    private String descricao;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;

    public TipoOfertaVO(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public TipoOfertaVO() {
    }
    
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
        this.descricao = Utils.acertarTexto(descricao, 20, "SEM DESCRICAO");
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }
    
    
    
}
