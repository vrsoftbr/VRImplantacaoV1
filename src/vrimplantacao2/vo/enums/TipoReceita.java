package vrimplantacao2.vo.enums;

/**
 *
 * @author Leandro
 */
public class TipoReceita {
    
    public static final TipoReceita CR_OUTRAS_UNIDADES = new TipoReceita(1, "CR-OUTRAS UNIDADES");

    private int id;
    private String descricao;

    public TipoReceita() {
    }

    public TipoReceita(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
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
        this.descricao = descricao;
    }

}
